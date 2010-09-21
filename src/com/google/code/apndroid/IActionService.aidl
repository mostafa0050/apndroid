package com.google.code.apndroid;

import android.os.Bundle;

interface IActionService {
	Bundle getStatus();

	Bundle switchStatus(in Bundle requestExtras);
}
