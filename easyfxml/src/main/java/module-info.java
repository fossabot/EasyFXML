open module moe.tristan.easyfxml {
    exports moe.tristan.easyfxml;
    exports moe.tristan.easyfxml.api;
    exports moe.tristan.easyfxml.util;
    exports moe.tristan.easyfxml.model.beanmanagement;
    exports moe.tristan.easyfxml.model.components.listview;
    exports moe.tristan.easyfxml.model.exception;
    exports moe.tristan.easyfxml.model.fxml;
    exports moe.tristan.easyfxml.model.system;

    requires java.annotation;

    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;

    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;

    requires transitive io.vavr;
    requires transitive slf4j.api;
    requires awaitility;

}
