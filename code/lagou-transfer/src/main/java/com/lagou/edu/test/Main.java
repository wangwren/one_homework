package com.lagou.edu.test;

import com.lagou.edu.factory.AnnotationFactory;
import com.lagou.edu.service.TransferService;

public class Main {

    public static void main(String[] args) {
        TransferService transferService = (TransferService) AnnotationFactory.getBean("TransferServiceImpl");

        System.out.println(transferService);
    }
}
