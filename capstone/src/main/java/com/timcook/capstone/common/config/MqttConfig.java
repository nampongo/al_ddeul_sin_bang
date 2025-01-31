package com.timcook.capstone.common.config;

import java.util.Objects;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;

import com.timcook.capstone.common.mqtt.MqttUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;

@Configuration
@RequiredArgsConstructor
public class MqttConfig {
//	private static final String MQTT_USERNAME = "admin";
//	private static final String MQTT_PASSWORD = "hivemq";
	private static final String MQTT_USERNAME = "shinmc";
	private static final String MQTT_PASSWORD = "1234";
	private static final String BROKER_URL = "tcp://localhost:1883";
	private static final String MQTT_PUB_CLIENT_ID = MqttAsyncClient.generateClientId();
	private static final String MQTT_SUB_CLIENT_ID = MqttAsyncClient.generateClientId();
	private static final String TOPIC_FILTER = "my/test/topic";
	
	private final MqttUtils mqttUtils;
	
	// MQTT 
	
	private MqttConnectOptions connectOptions() {
		MqttConnectOptions options = new MqttConnectOptions();
		options.setServerURIs(new String[] {BROKER_URL});
		options.setUserName(MQTT_USERNAME);
		options.setPassword(MQTT_PASSWORD.toCharArray());
		return options;
	}
	
	@Bean
	public DefaultMqttPahoClientFactory defaultMqttPahoClientFactory() {
		DefaultMqttPahoClientFactory clientFactory = new DefaultMqttPahoClientFactory();
		clientFactory.setConnectionOptions(connectOptions());
		return clientFactory;
	}
	
	// inboundChannel
	// -> inbound channel adapter -> inbound channel -> service activator -> message
	
	@Bean
	public MessageChannel mqttInputChannel(){
		return new DirectChannel();
	}
	
	@Bean
	public MessageProducer inboundChannel() {
		MqttPahoMessageDrivenChannelAdapter adapter =
				new MqttPahoMessageDrivenChannelAdapter(BROKER_URL, MQTT_SUB_CLIENT_ID, TOPIC_FILTER);
		adapter.setCompletionTimeout(5000);
		adapter.setConverter(new DefaultPahoMessageConverter());
		adapter.setQos(1);
		adapter.setOutputChannel(mqttInputChannel());
		return adapter;
	}
	
	@Bean
	@ServiceActivator(inputChannel = "mqttInputChannel")
	public MessageHandler inboundMessageHandler() {
		return message -> {
			String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
			String payload = (String) message.getPayload();
			
			mqttUtils.payloadToMessage(Objects.requireNonNull(payload));
		};
	}
	
	// outboundChannel
	// gateway -> outbound channel -> outbound channel adapter -> message 
	
	@Bean
	public MessageChannel mqttOutboundChannel() {
		return new DirectChannel();
	}
	
	@Bean
	@ServiceActivator(inputChannel = "mqttOutboundChannel") 
	public MessageHandler mqttOutbound(DefaultMqttPahoClientFactory clientFactory) {
		MqttPahoMessageHandler messageHandler =
				new MqttPahoMessageHandler(MQTT_PUB_CLIENT_ID, clientFactory);
		messageHandler.setAsync(true);
		messageHandler.setDefaultQos(1);
		return messageHandler;
	}
	
	@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
	public interface OutboundGateWay{
		void sendToMqtt(String payload, @Header(MqttHeaders.TOPIC) String topic);
	}
	
}
