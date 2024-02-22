package com.vong.manidues;

import java.util.Scanner;

public class Solution {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        int b = sc.nextInt();
        int h = sc.nextInt();

        if (b < 0 || h < 0) {
            System.out.println(
                    "java.lang.Exception: Breadth and height must be positive");
            return;
        }

        System.out.println(b * h);



    }
}