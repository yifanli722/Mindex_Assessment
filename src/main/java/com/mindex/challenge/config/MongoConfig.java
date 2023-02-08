package com.mindex.challenge.config;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.PostConstruct;

@EnableMongoRepositories(basePackageClasses = EmployeeRepository.class)
@Configuration
public class MongoConfig{

    @Autowired
    private MongoClient mongoClient;

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoDbFactory(mongoClient));
    }

    @Bean
    public MongoDbFactory mongoDbFactory(MongoClient mongoClient) {
        return new SimpleMongoClientDbFactory(mongoClient, "test");
    }

    @Bean(destroyMethod="shutdown")
    public MongoServer mongoServer() {
        MongoServer mongoServer = new MongoServer(new MemoryBackend());
        mongoServer.bind();
        return mongoServer;
    }

    @Bean(destroyMethod="close")
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb:/" + mongoServer().getLocalAddress());
    }

    @PostConstruct
    public void createUniqueIndex() {
        MongoTemplate mongoTemplate = mongoTemplate(mongoClient);
        mongoTemplate.indexOps(Compensation.class).ensureIndex(new Index().on("employeeId", Sort.Direction.ASC).unique());
    }
}


