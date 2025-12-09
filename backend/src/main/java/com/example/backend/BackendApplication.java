package com.example.backend;
import com.example.backend.catedra.CatedraService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    // ESTE ES EL BLOQUE MÁGICO QUE TE FALTA
    @Bean
    public CommandLineRunner initData(CatedraService catedraService) {
        return args -> {
            System.out.println("🚀 SERVIDOR INICIADO: Intentando conectar con la Cátedra...");
            try {
                catedraService.sincronizarEventos();
                System.out.println("✅ ¡Sincronización inicial completada con éxito!");
            } catch (Exception e) {
                System.err.println("❌ Error grave en la sincronización: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}