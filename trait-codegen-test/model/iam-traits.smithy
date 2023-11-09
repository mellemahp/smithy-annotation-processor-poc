$version: "2.0"

namespace aws.iam

/// Indicates properties of a Smithy operation as an IAM action.
@trait(selector: "operation")
structure iamAction {
    /// The name of the action in AWS IAM.
    name: String

    /// A brief description of what granting the user permission to invoke an operation would entail.
    /// This description should begin with something similar to 'Enables the user to...' or 'Grants permission to...'
    documentation: String

    /// A relative URL path that defines more information about the action within a set of IAM-related documentation.
    relativeDocumentation: String

    /// Other actions that the invoker must be authorized to perform when executing the targeted operation.
    requiredActions: RequiredActionsList

    /// The resources an IAM action can be authorized against.
    resources: ActionResources

    /// The resources that performing this IAM action will create.
    createsResources: ResourceNameList
}

/// A container for information on the resources that an IAM action may be authorized against.
@private
structure ActionResources {
    /// Resources that will always be authorized against for functionality of the IAM action.
    required: ActionResourceMap

    /// Resources that will be authorized against based on optional behavior of the IAM action.
    optional: ActionResourceMap
}

@private
map ActionResourceMap {
    key: ResourceName
    value: ActionResource
}

/// Contains information about a resource an IAM action can be authorized against.
@private
structure ActionResource {
    /// The condition keys used for authorizing against this resource.
    conditionKeys: ConditionKeysList
}

@private
@uniqueItems
list ConditionKeysList {
    member: String
}

@private
@uniqueItems
list RequiredActionsList {
    member: IamIdentifier
}

@private
@uniqueItems
list ResourceNameList {
    member: ResourceName
}

@private
string IamIdentifier

@private
string ResourceName

@private
string ResourceName
