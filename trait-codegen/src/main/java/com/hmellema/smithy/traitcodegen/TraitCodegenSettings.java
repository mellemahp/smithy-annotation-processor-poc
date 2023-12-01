package com.hmellema.smithy.traitcodegen;

import software.amazon.smithy.model.node.ObjectNode;

import java.util.List;

public record TraitCodegenSettings(String packageName, List<String> headerLines) {
    public static TraitCodegenSettings from(ObjectNode settingsNode) {
        return new TraitCodegenSettings(
                settingsNode.expectStringMember("package").getValue(),
                settingsNode.expectArrayMember("header")
                        .getElementsAs(el -> el.expectStringNode().getValue())
        );
    }
}
