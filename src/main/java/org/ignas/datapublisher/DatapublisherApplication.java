package org.ignas.datapublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DatapublisherApplication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(DatapublisherApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DatapublisherApplication.class, args);
	}

	@Autowired
	private Publisher publisher;

	@Override
	public void run(String... args) {
		LOG.debug("Starting publishing!");

		while (true) {
			Integer processed = publisher.publishBatch(1000);
			if (processed == 0) {
				break;
			}
		}
	}
}
