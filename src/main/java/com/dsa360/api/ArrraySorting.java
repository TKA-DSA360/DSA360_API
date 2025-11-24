package com.dsa360.api;

import java.util.Arrays;

public class ArrraySorting {

	public static void main(String[] args) {

		int a[] = { 5, 9, 4, 16, 7 };

	for (int i = 0; i < a.length-1; i++) {
			
			for (int j = i+1; j < a.length; j++) {
				
				 int t = 0;
				 if(a[i]<a[j]) {
					 t=a[i];
					 a[i]=a[j];
					 a[j]=t;
					};
			}
			
		}
		System.out.println(Arrays.toString(a));
		
	}

}
