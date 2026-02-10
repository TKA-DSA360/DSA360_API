package com.dsa360.api;

public class ReverseArray {

	public static void main(String[] args) {
		
		int arr[]= {2,5,7,8,5};
		
		
System.out.println("Original Array:");
		
		for (int i = 0; i < arr.length; i++) {
			
			System.out.print(arr[i]+" ");
		}
		
		System.out.println("\nReverse Array:");
		
		for (int i = arr.length-1; i >=0 ; i--) {
			System.out.print(arr[i]+" ");
		}

}
	
}
	
