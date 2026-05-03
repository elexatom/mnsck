package cz.zcu.kiv.elexa.mnsck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hlavní třída pro spuštění Spring Boot aplikace MNSCK.
 * 
 * @author Tomáš Elexa
 */
@SpringBootApplication
public class MnsckApplication {

	/**
	 * Hlavní metoda pro spuštění aplikace.
	 * 
	 * @param args argumenty příkazové řádky
	 */	
	public static void main(String[] args) {
		SpringApplication.run(MnsckApplication.class, args);
	}

}
