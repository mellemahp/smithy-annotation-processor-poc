$version: "2.0"

namespace test.smithy.traitcodegen

/// A basic annotation trait
@trait(selector: "structure > :test(member > string)")
structure basicAnnotationTrait {}

/// Simple String trait
@trait(selector: "member")
string stringTrait

// ===============
//  Number traits
// ===============
@trait
integer HttpCodeInteger

@trait
long HttpCodeLong

@trait
short HttpCodeShort

@trait
float HttpCodeFloat

@trait
double HttpCodeDouble

// ===========
// List traits
// ===========
/// A list with only a simple string member
@trait
list stringListTrait {
    member: MyString
}

string MyString
