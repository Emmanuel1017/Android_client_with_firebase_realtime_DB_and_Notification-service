package com.cariboudevs.ccoudoorske.Utility.exceptions;

import android.content.res.Resources.NotFoundException;

import com.cariboudevs.ccoudoorske.Utility.Notifications;

public class NotifyDefaultChannelInfoNotFoundException extends NotFoundException {
    public NotifyDefaultChannelInfoNotFoundException(){}
    @Override
    public String getMessage() {
        return "One or more of the next values is missing from string resources: " +
                Notifications.ChannelData.ID+", " +
                Notifications.ChannelData.NAME+" or " +
                Notifications.ChannelData.DESCRIPTION;
    }
}
