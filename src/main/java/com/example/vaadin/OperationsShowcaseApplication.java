package com.example.vaadin;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 社内オペレーション向けデモアプリケーションのエントリーポイント。
 */
@SpringBootApplication
@Theme("fixedlayout")
public class OperationsShowcaseApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(OperationsShowcaseApplication.class, args);
    }
}
