package com.app.proj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.proj.entities.PreRegister;
import com.app.proj.repository.PreRegisterRepository;

@Service
public class PreRegisterService {

    @Autowired
    private PreRegisterRepository preRegisterRepository;

    public PreRegister createPreRegister(PreRegister preRegister) {
        return preRegisterRepository.save(preRegister);
    }
}
