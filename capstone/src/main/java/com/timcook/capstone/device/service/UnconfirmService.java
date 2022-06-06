package com.timcook.capstone.device.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.timcook.capstone.device.domain.Device;
import com.timcook.capstone.device.domain.Disabled;
import com.timcook.capstone.device.domain.Unconfirm;
import com.timcook.capstone.device.repository.DeviceRepository;
import com.timcook.capstone.device.repository.UnconfirmRepository;
import com.timcook.capstone.file.domain.File;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnconfirmService {

	private final UnconfirmRepository unconfirmRepository;
	private final DeviceRepository deviceRepository;
	
	@Transactional(propagation = Propagation.SUPPORTS)
	public void save(File file, List<Long> devicesId) {
		log.info("UNCONFIRM INFO SAVE");
		List<Device> devices = deviceRepository.findAllById(devicesId);
		
		List<Unconfirm> list = new ArrayList<>();
		
		for(Device device : devices){
			log.info("deviceId={}",device.getId());
			list.add(Unconfirm.builder().device(device).file(file).build());
		}
		
		unconfirmRepository.saveAll(list);
		
	}
	
}
