package com.kevinlee.demo;

import org.springframework.stereotype.Component;

@Component
public class Dog implements Animal{
    @Override
    public String getName() {
        return "this is Dog";
    }
}
