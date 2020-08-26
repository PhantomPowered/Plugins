package com.github.phantompowered.plugins.betterlogin.connection;

import com.github.phantompowered.proxy.api.player.PlayerAbilities;

public class LoginPlayerAbilities implements PlayerAbilities {
    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean isFlying() {
        return false;
    }

    @Override
    public boolean isAllowedFlying() {
        return true;
    }

    @Override
    public boolean isCreativeMode() {
        return true;
    }

    @Override
    public float getFlightSpeed() {
        return 0;
    }

    @Override
    public float getWalkSpeed() {
        return 0;
    }

    @Override
    public void setFlying(boolean flying) {
    }
}
