package com.here.maps.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ImportResource;

/**
 * @author Vidyashri
 *
 */
@SpringBootApplication
@EnableCaching
@ImportResource("classpath*:applicationcontext.xml")
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
		CassandraDataAutoConfiguration.class })
public class HeremapsusecaseapplicationApplication {
	/**
	 * Main method.
	 */

	public static void main(String[] args) {

		SpringApplication.run(HeremapsusecaseapplicationApplication.class, args);

	}
}