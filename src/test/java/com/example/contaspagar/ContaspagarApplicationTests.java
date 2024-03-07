package com.example.contaspagar;

import com.example.contaspagar.controller.ContaController;
import com.example.contaspagar.repository.ContaRepository;
import com.example.contaspagar.service.ContaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ContaspagarApplicationTests {

	@Autowired
	private ContaController contaController;

	@Autowired
	private ContaService contaService;

	@Autowired
	private ContaRepository contaRepository;

	@Test
	void contextLoads() {
		assertNotNull(contaController, "A ContaController não está sendo injetada corretamente no contexto.");
		assertNotNull(contaService, "A ContaService não está sendo injetada corretamente no contexto.");
		assertNotNull(contaRepository, "A ContaRepository não está sendo injetada corretamente no contexto.");

	}
}
