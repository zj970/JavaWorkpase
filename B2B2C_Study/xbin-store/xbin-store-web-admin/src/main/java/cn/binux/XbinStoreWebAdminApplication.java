package cn.binux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;


@Configuration
//@EnableApolloConfig
@SpringBootApplication
//@EnableWebMvc
public class XbinStoreWebAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(XbinStoreWebAdminApplication.class, args);
	}
}
