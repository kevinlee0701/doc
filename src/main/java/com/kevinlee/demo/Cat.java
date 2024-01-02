package kevinlee.demo;

import org.springframework.stereotype.Component;

@Component
public class Cat implements Animal{

    @Override
    public String getName() {
        return "this is cat";
    }
}
