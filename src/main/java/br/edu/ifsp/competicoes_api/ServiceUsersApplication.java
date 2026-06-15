package br.edu.ifsp.competicoes_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = {
        "br.edu.ifsp.competicoes_api"
    },
    exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class, 
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
    }
)
@EnableJpaRepositories(basePackages = "br.edu.ifsp.competicoes_api.repository")
@EntityScan(basePackages = "br.edu.ifsp.competicoes_api.model")
public class ServiceUsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceUsersApplication.class, args);
    }

}