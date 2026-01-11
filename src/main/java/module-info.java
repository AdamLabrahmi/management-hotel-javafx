module com.emsi.mh.mangmenthotel {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires static lombok;

    opens com.emsi.mh.mangmenthotel to javafx.fxml;
    opens com.emsi.mh.mangmenthotel.controller to javafx.fxml;
    opens com.emsi.mh.mangmenthotel.model;

    exports com.emsi.mh.mangmenthotel;
    exports com.emsi.mh.mangmenthotel.model;
}
