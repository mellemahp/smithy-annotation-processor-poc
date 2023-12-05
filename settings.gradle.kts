
rootProject.name = "trait-codegen-poc"
include("smithy-annotation-processor")
include(":smithy-trait-codegen:processor:annotation", ":smithy-trait-codegen:processor:processor")
include(":smithy-trait-codegen:codegen", ":smithy-trait-codegen:codegen-test")