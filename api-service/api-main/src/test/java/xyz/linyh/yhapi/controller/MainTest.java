package xyz.linyh.yhapi.controller;

import org.aspectj.weaver.ast.Var;
import xyz.linyh.yhapi.controller.test.Generics;

import java.lang.reflect.Field;

public class MainTest {
    public static void main(String[] args) throws Exception{
//        Integer a = 127;
//        Integer b = 127;
//        System.out.println(a == b);
//
////        自动装箱
//        Integer c = 128;
//        Integer d = 128;
//        System.out.println(c == d);
//
//        Integer e = 127;
//        int a1 = Character.digit('a', 16);
//        System.out.println(a1);

        Generics<String> stringGenerics = new Generics<>();
        stringGenerics.setName("123");
        System.out.println(stringGenerics.getName());

        Class<? extends Generics> clazz = stringGenerics.getClass();
        Field field = clazz.getDeclaredField("name");
        field.setAccessible(true);
        field.set(stringGenerics, "456");
        System.out.println(stringGenerics.getName());


    }
}
