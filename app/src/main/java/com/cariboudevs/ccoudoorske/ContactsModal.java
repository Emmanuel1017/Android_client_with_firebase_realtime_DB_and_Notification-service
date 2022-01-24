package com.cariboudevs.ccoudoorske;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactsModal implements Parcelable {
    //creating variables for our different fields.
    private String Name;
    private String Email;
    private String Mobile_Number;
    private String Location;
    private String Services;
    private String Date;
    private String Description;
    private String ContactId;
    private Long TimeStamp;




    //getters
    public String getName() {
        return Name;
    }
    public String getEmail() {
        return Email;
    }
    public String getMobile_Number() {
        return Mobile_Number;
    }
    public String getLocation() {
        return Location;
    }
    public String getServices() { return Services; }
    public String getDate() {
        return Date;
    }
    public String getDescription() {
        return Description;
    }
    public String getContactId() {
        return ContactId;
    }
    public Long getTimeStamp() {
        return TimeStamp;
    }


    //setters
    public void setName(String name) {
        this.Name = name;
    }
    public void setEmail(String email) {
        this.Email = email;
    }
    public void setMobile_Number(String mobile_number) {
        this.Mobile_Number = mobile_number;
    }
    public void setLocation(String location) { this.Location = location; }
    public void setServices(String services) {
        this.Services = services;
    }
    public void setDate(String date) {
        this.Date = date;
    }
    public void setDescription(String description) {
        this.Description = description;
    }
    public void setContactId(String contactid) {
        this.ContactId = contactid;
    }
    public void setTimeStamp(Long timeStamp) {
        this.TimeStamp = timeStamp;
    }


    //creating an empty constructor.
    public ContactsModal() {

    }

    protected ContactsModal(Parcel in) {
        Name = in.readString();
        Email = in.readString();
        Mobile_Number = in.readString();
        Location = in.readString();
        Services = in.readString();
        Date = in.readString();
        Description = in.readString();
        ContactId= in.readString();
        TimeStamp= in.readLong();
    }

    public static final Creator<ContactsModal> CREATOR = new Creator<ContactsModal>() {
        @Override
        public ContactsModal createFromParcel(Parcel in) {
            return new ContactsModal(in);
        }

        @Override
        public ContactsModal[] newArray(int size) {
            return new ContactsModal[size];
        }
    };



    public ContactsModal(String Name, String Email, String Mobile_Number, String Location, String Services, String Date, String Description,String ContactId,Long TimeStamp)  {
        this.Name = Name;
        this.Email = Email;
        this.Mobile_Number = Mobile_Number;
        this.Location = Location;
        this.Services = Services;
        this.Date = Date;
        this.Description = Description;
        this.ContactId =ContactId;
        this.TimeStamp = TimeStamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(Email);
        dest.writeString(Mobile_Number);
        dest.writeString(Location);
        dest.writeString(Services);
        dest.writeString(Date);
        dest.writeString(Description);
        dest.writeString(ContactId);
        dest.writeLong(TimeStamp);
    }
}
