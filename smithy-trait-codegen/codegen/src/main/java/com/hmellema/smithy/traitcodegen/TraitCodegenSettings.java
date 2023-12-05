package com.hmellema.smithy.traitcodegen;

import software.amazon.smithy.model.node.ObjectNode;

import java.util.List;

public final class TraitCodegenSettings {
    private final String packageName;
    private final List<String>  headerLines;

    TraitCodegenSettings(String packageName, List<String> headerLines) {
        this.packageName = packageName;
        this.headerLines = headerLines;
    }

    public String packageName() {
        return packageName;
    }

    public List<String> headerLines() {
        return headerLines;
    }

    public static TraitCodegenSettings from(ObjectNode settingsNode) {
        return new TraitCodegenSettings(
                settingsNode.expectStringMember("package").getValue(),
                settingsNode.expectArrayMember("header")
                        .getElementsAs(el -> el.expectStringNode().getValue())
        );
    }
}