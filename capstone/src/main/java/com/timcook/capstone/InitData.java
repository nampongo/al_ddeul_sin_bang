package com.timcook.capstone;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.catalina.startup.UserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.timcook.capstone.admin.domain.Admin;
import com.timcook.capstone.device.domain.Device;
import com.timcook.capstone.device.domain.Status;
import com.timcook.capstone.message.domain.DetectMessage;
import com.timcook.capstone.message.domain.UrgentMessage;
import com.timcook.capstone.user.domain.Role;
import com.timcook.capstone.user.domain.User;
import com.timcook.capstone.village.domain.Address;
import com.timcook.capstone.village.domain.Location;
import com.timcook.capstone.village.domain.Village;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitData {

	private final InitService initService;
	
	@PostConstruct
	public void init() {
		initService.init();
	}
	
	@Component
	public static class InitService{
		
		@PersistenceContext
		EntityManager em;
		
		@Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;
		
		@Transactional
		public void init() {
			
			Village village = Village.builder()
					.address(Address.builder().city("서울시").state("광진구").town("화양동").build())
					.nickname("화양동사무소")
					.location(Location.builder().longitude(37.542914462924).latitude(127.06868274185).build())
					.build();
			
			String password = bCryptPasswordEncoder.encode("password");
			
			Admin admin = new Admin("test", password,"test", Role.ROLE_ADMIN, null, null, null, "01012341234", null);
			admin.registerVillage(village);
			
			Admin admin2 = new Admin("test2", password,"test2", Role.ROLE_ADMIN, null, null, null, "01012341234", null);
			em.persist(admin);
			em.persist(admin2);
			em.persist(village);
			em.flush();
			
			
			for (int i=0;i<3;i++) {
				Device device = Device.builder()
									.village(village)
									.build();
				
				User user = User.builder()
						.username("devuser" + Integer.toString(i+1))
						.password(password)
						.email("user" + Integer.toString(i+1))
						.phoneNumber(Integer.toString(i))
						.role(Role.ROLE_USER)
						.device(device)
						.build();
				
				DetectMessage detectMessage = DetectMessage.builder()
										.device(device)
										.temperature(1.1)
										.humidity(1.1)
										.detectionVibration(true)
										.detectionGasLeak(false)
										.detectionAbnormalness(false)
										.build();
				
				DetectMessage detectMessage2 = DetectMessage.builder()
						.device(device)
						.temperature(2.1)
						.humidity(2.1)
						.detectionVibration(false)
						.detectionGasLeak(true)
						.detectionAbnormalness(false)
						.build();
				
				for(int j=0;j<5;j++) {
					UrgentMessage urgentMessage = UrgentMessage.builder()
							.device(device)
							.build();
					
					device.addUrgentMessage(urgentMessage);
					
					em.persist(urgentMessage);
				}
				
				device.changeInfo(user, village);
				user.registerVillage(village);
				
				User guardian = User.builder()
						.username("guar" + Integer.toString(i+1))
						.password(password)
						.email("guar" + Integer.toString(i+1))
						.village(village)
						.phoneNumber("0" + Integer.toString(i+1))
						.role(Role.ROLE_USER)
						.build();
				
				
				user.addGaurdian(guardian);
				
				em.persist(guardian);
				em.persist(user);
				em.persist(detectMessage);
				em.persist(detectMessage2);
				em.persist(device);
				em.flush();
			}
			for (int i=3;i<6;i++) {
				User user = User.builder()
						.username("tmpuser" + Integer.toString(i+1))
						.password(password)
						.email("user" + Integer.toString(i+1))
						.phoneNumber(Integer.toString(i))
						.role(Role.ROLE_USER)
						.build();
				
				user.registerVillage(village);
				
				em.persist(user);
				em.flush();
			}
			 
			User user = User.builder()
					.username("mqtt")
					.password(password)
					.email("mqtt")
					.phoneNumber("112")
					.role(Role.ROLE_USER)
					.build();
			
			Device device = Device.builder()
					.village(village)
					.build();
			
			
			
			em.persist(user);
			em.persist(device);
			
			em.persist(village);
			em.flush();
		}
		
	}
	
}
