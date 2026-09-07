package com.danielradu;

import com.danielradu.customer.Customer;
import com.danielradu.customer.CustomerRepository;
import com.danielradu.customer.Gender;
import com.danielradu.s3.S3Buckets;
import com.danielradu.s3.S3Service;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Random;

@SpringBootApplication
@EnableConfigurationProperties(S3Buckets.class)
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            S3Service s3Service,
            S3Buckets s3Buckets) {
        return args -> {
            createRandomCustomer(customerRepository, passwordEncoder);
          //  testBucketUploadAndDownload(s3Service, s3Buckets);
        };
    }

    public static void createRandomCustomer(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        var faker = new Faker();
        Random random = new Random();
        Name name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();
        int age = random.nextInt(16, 99);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@amigoscode.com";
        Customer customer = new Customer(
                firstName +  " " + lastName,
                email,
                passwordEncoder.encode("password"),
                age,
                gender);
        customerRepository.save(customer);
        System.out.println(email);
    }

    public static void testBucketUploadAndDownload(S3Service s3Service, S3Buckets s3Buckets) throws IOException {
        s3Service.putObject(
                s3Buckets.getCustomer(),
                "foo/bar/jamila",
                "Hello World!".getBytes()
        );

        try {
            byte[] object = s3Service.getObject(s3Buckets.getCustomer(), "foo");
            System.out.println("Hooray " + new String(object));
        } catch (IOException e) {
            System.out.println("Failed to retrieve object");
            throw new RuntimeException(e);
        }
    }

}
