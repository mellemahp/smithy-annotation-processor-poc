package com.hmellema.smithy.traitcodegen;

import software.amazon.smithy.model.node.ObjectNode;
public record TraitCodegenSettings(String packageName) {
    public static TraitCodegenSettings from(ObjectNode settingsNode) {
        return new TraitCodegenSettings(settingsNode.expectStringMember("package").getValue());
    }
}
