/*
 *  weMessage - iMessage for Android
 *  Copyright (C) 2018 Roman Scott
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package scott.wemessage.app.models.users;

import android.content.res.Resources;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.List;
import java.util.UUID;

import scott.wemessage.app.utils.FileLocationContainer;
import scott.wemessage.commons.utils.StringUtils;

public class Contact extends ContactInfo {

    private UUID uuid;
    private String firstName, lastName;
    private List<Handle> handles;
    private Handle primaryHandle;
    private FileLocationContainer contactPictureFileLocation;

    public Contact(){

    }

    public Contact(UUID uuid, String firstName, String lastName, List<Handle> handles, Handle primaryHandle, FileLocationContainer contactPictureFileLocation) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.handles = handles;
        this.primaryHandle = primaryHandle;
        this.contactPictureFileLocation = contactPictureFileLocation;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getDisplayName(){
        try {
            String fullString = "";

            if (!StringUtils.isEmpty(getFirstName())) {
                fullString = getFirstName();
            }

            if (!StringUtils.isEmpty(getLastName())) {
                fullString += " " + getLastName();
            }

            if (StringUtils.isEmpty(fullString)) {
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

                try {
                    Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(getPrimaryHandle().getHandleID(), Resources.getSystem().getConfiguration().locale.getCountry());

                    fullString = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                }catch (Exception ex){
                    fullString = getPrimaryHandle().getHandleID();
                }
            }

            return fullString;
        }catch(Exception ex){
            return "";
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public List<Handle> getHandles() {
        return handles;
    }

    public Handle getPrimaryHandle(){
        return (primaryHandle == null) ? handles.get(0) : primaryHandle;
    }

    public FileLocationContainer getContactPictureFileLocation() {
        return contactPictureFileLocation;
    }

    @Override
    public ContactInfo findRoot() {
        return this;
    }

    @Override
    public Handle pullHandle(boolean iMessage){
        if (iMessage){
            if (getPrimaryHandle().getHandleType() == Handle.HandleType.IMESSAGE) return getPrimaryHandle();
            else {
                for (Handle h : getHandles()){
                    if (h.getHandleType() == Handle.HandleType.IMESSAGE){
                        return h;
                    }
                }
                return getPrimaryHandle();
            }
        }else {
            return getPrimaryHandle();
        }
    }

    public Contact setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public Contact setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public Contact setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Contact setHandles(List<Handle> handles) {
        this.handles = handles;
        return this;
    }

    public Contact setPrimaryHandle(Handle handle){
        this.primaryHandle = handle;
        return this;
    }

    public Contact addHandle(Handle handle){
        handles.add(handle);
        return this;
    }

    public Contact removeHandle(Handle handle){
        for (Handle h : handles){
            if (h.getUuid().toString().equals(handle.getUuid().toString())){
                if (handle.equals(primaryHandle)){
                    primaryHandle = null;
                }
                handles.remove(h);
                return this;
            }
        }
        return this;
    }

    public Contact setContactPictureFileLocation(FileLocationContainer contactPictureFileLocation) {
        this.contactPictureFileLocation = contactPictureFileLocation;
        return this;
    }
}