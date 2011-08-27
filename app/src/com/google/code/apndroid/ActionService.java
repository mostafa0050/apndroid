/*
 * This file is part of APNdroid.
 *
 * APNdroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * APNdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with APNdroid. If not, see <http://www.gnu.org/licenses/>.
 */

package com.google.code.apndroid;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author wingphil
 */
public class ActionService extends Service {

    private IBinder remoteBinder = new IActionService.Stub() {
        public synchronized Bundle switchStatus(Bundle requestExtras) throws RemoteException {
            Log.i(Constants.APP_LOG, "ActionService.switchStatus called");
            Bundle result = ActionUtils.processSwitchRequest(ActionService.this, requestExtras);
            return result;
        }

        public synchronized Bundle getStatus() throws RemoteException {
            Log.i(Constants.APP_LOG, "ActionService.getStatus called");
            Bundle result = ActionUtils.processStatusRequest(ActionService.this);
            return result;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(Constants.APP_LOG, "Client has bound to service");
        return remoteBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(Constants.APP_LOG, "All clients have unbound from service");
        return false;
    }

    // @Override
    // public int onStartCommand(Intent intent, int flags, int startId) {
    // return START_STICKY;
    // }

}
