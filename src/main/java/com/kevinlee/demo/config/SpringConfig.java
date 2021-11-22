package com.kevinlee.demo.config;

import com.kevinlee.demo.Animal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class SpringConfig {
    @Autowired
    private List<Animal> list ;

    @Bean("classroomLiveInfoResolverMap")
    public Map<String, Animal> resolverMap(ConfigurableListableBeanFactory beanFactory) {
        Map<String, Animal> resolverMap = beanFactory.getBeansOfType(Animal.class);
        for (Animal animal : list) {
            System.out.println(animal);
        }
        return resolverMap.values().stream().collect(Collectors.toMap(Animal::getName, Function.identity()));

    }
}
