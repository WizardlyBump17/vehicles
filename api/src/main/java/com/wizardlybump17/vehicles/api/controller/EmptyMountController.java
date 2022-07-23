package com.wizardlybump17.vehicles.api.controller;

import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.mount.controller.AbstractMountController;
import com.ticxo.modelengine.api.nms.WrapperLookController;
import com.ticxo.modelengine.api.nms.WrapperMoveController;

public class EmptyMountController extends AbstractMountController {

    @Override
    public void updateDirection(WrapperLookController controller, ModeledEntity model) {
    }

    @Override
    public void updateMovement(WrapperMoveController controller, ModeledEntity model) {
    }

    @Override
    public void updatePassengerMovement(WrapperMoveController controller, ModeledEntity model) {
    }

    @Override
    public EmptyMountController getInstance() {
        return new EmptyMountController();
    }
}
