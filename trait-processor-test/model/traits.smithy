$version: "2.0"

namespace io.smithy.example

/// A JSON name
@trait(selector: "member")
string jsonName

@trait
structure myComplexTrait {
    fieldA: String
    fieldB: Boolean
    fieldC: NestedA
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
