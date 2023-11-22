$version: "2.0"

namespace io.smithy.example

/// A JSON name
@trait(selector: "member")
string jsonName

@trait
structure myComplexTrait {
    @required
    fieldA: String

    fieldB: Boolean

    fieldC: NestedA

    fieldD: ListD

    fieldE: MyMap
}

list ListD {
    member: MyString
}

@private
map MyMap {
    key: NonEmptyString
    value: NonEmptyString
}

structure NestedA {
    @required
    fieldN: String

    fieldQ: Boolean

    fieldZ: NestedB
}

enum NestedB {
    A
    B
}

/// An HttpCode
@trait
integer HttpCode

/// An HttpCode
@trait
short HttpCodeShort

/// An HttpCode
@trait
long HttpCodeLong

@documentation("A list of strings.")
@trait
list strList {
    member: MyString
}

string MyString

/// A simple enum trait
@trait(selector: "structure > member")
enum myEnum {
    /// Positive response
    YES = "yes"

    /// Negative response
    NO = "no"
}

/// A simple enum trait
@trait(selector: "structure > member")
intEnum myIntEnum {
    /// Positive response
    YES = 1

    /// Negative response
    NO = 2
}

/// Defines the ordered list of supported authentication schemes.
@trait(selector: ":is(service, operation)")
list auth {
    member: AuthTraitReference
}

/// A string that must target an auth trait.
@idRef(selector: "[trait|authDefinition]")
@private
string AuthTraitReference

@trait(selector: "operation")
list examples {
    member: Example
}

@private
structure Example {
    @required
    title: String

    documentation: String
}

@trait
@length(min: 1)
map externalDocumentationIsh {
    key: NonEmptyString
    value: AuthTraitReference
}

@private
@length(min: 1)
string NonEmptyString
