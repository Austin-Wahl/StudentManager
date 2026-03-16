package models;

import java.util.UUID;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class StudentTableRecord extends Student {

    private StringProperty uuidProp;
    public void setUUIDProp(UUID value) { uuidProperty().set(value.toString()); }
    public String getUuidProp() { return uuidProperty().get(); }
    public StringProperty uuidProperty() { 
        if (uuidProp == null) uuidProp = new SimpleStringProperty(this, "uuid");
        return uuidProp; 
    }

    private StringProperty nameProp;
    public void setNameProp(String value) { nameProperty().set(value); }
    public String getNameProp() { return nameProperty().get(); }
    public StringProperty nameProperty() { 
        if (nameProp == null) nameProp = new SimpleStringProperty(this, "First Last");
        return nameProp; 
    }
     
    private StringProperty emailProp;
    public void setEmailProp(String value) { emailProperty().set(value); }
    public String getEmailProp() { return emailProperty().get(); }
    public StringProperty emailProperty() { 
        if (emailProp == null) emailProp = new SimpleStringProperty(this, "first@university.edu");
        return emailProp; 
    }

    private DoubleProperty gpaProp;
    public void setGpaProp(double GPA) { gpaProperty().set(GPA); }
    public double getGpaProp() { return gpaProperty().get(); }
    public DoubleProperty gpaProperty() { 
        if (gpaProp == null) gpaProp = new SimpleDoubleProperty(this, "-1");
        return gpaProp; 
    }
    
    public StudentTableRecord(UUID uuid, String name, String email, double GPA) {
        super(uuid, name, email, GPA);

        setUUIDProp(uuid);
        setNameProp(name);
        setEmailProp(email);
        setGpaProp(GPA);
    }
}