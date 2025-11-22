package com.dsa360.api;

import java.time.LocalDate;
import java.time.Period;

public class A {
public static void main(String[] args) {
	Period age = Period.between(LocalDate.of(1995,5,10), LocalDate.now());
	System.out.println(age);

}
}
