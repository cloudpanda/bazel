// Copyright 2014 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.rules.objc;

import static com.google.devtools.build.lib.packages.Attribute.ConfigurationTransition.HOST;
import static com.google.devtools.build.lib.packages.Attribute.attr;
import static com.google.devtools.build.lib.packages.Type.LABEL;
import static com.google.devtools.build.lib.packages.Type.LABEL_LIST;

import com.google.devtools.build.lib.packages.RuleClass;
import com.google.devtools.build.lib.packages.RuleClass.Builder;
import com.google.devtools.build.lib.util.FileType;
import com.google.devtools.build.lib.view.BaseRuleClasses;
import com.google.devtools.build.lib.view.BlazeRule;
import com.google.devtools.build.lib.view.RuleDefinition;
import com.google.devtools.build.lib.view.RuleDefinitionEnvironment;

/**
 * Rule definition for objc_proto_library.
 *
 * This is a temporary rule until it is better known how to support proto_library rules.
 */
@BlazeRule(name = "objc_proto_library",
    factoryClass = ObjcProtoLibrary.class,
    ancestors = { BaseRuleClasses.RuleBase.class })
public class ObjcProtoLibraryRule implements RuleDefinition {

  static final String OPTIONS_FILE_ATTR = "options_file";
  static final String COMPILE_PROTOS_ATTR = "$compile_protos";
  static final String PROTO_SUPPORT_ATTR = "$proto_support";
  static final String LIBPROTOBUF_ATTR = "$lib_protobuf";

  @Override
  public RuleClass build(Builder builder, final RuleDefinitionEnvironment env) {
    return builder
        /* <!-- #BLAZE_RULE(objc_proto_library).ATTRIBUTE(deps) -->
        The directly depended upon proto_library rules.
        <!-- #END_BLAZE_RULE.ATTRIBUTE -->*/
        .override(attr("deps", LABEL_LIST)
            .allowedRuleClasses("proto_library", "filegroup")
            .legacyAllowAnyFileType())
        /* <!-- #BLAZE_RULE(objc_proto_library).ATTRIBUTE(options_file) -->
        Optional options file to apply to protos which affects compilation (e.g. class
        whitelist/blacklist settings).
        <!-- #END_BLAZE_RULE.ATTRIBUTE -->*/
        .add(attr(OPTIONS_FILE_ATTR, LABEL).legacyAllowAnyFileType().singleArtifact().cfg(HOST))
        .add(attr(COMPILE_PROTOS_ATTR, LABEL)
            .allowedFileTypes(FileType.of(".py"))
            .cfg(HOST)
            .singleArtifact()
            .value(env.getLabel("//tools/objc:compile_protos")))
        .add(attr(PROTO_SUPPORT_ATTR, LABEL).legacyAllowAnyFileType().cfg(HOST)
            .value(env.getLabel("//tools/objc:proto_support")))
        // TODO(bazel-team): Use //external:objc_proto_lib when bind() support is a little better
        .add(attr(LIBPROTOBUF_ATTR, LABEL).allowedRuleClasses("objc_library")
            .value(env.getLabel(
                "//googlemac/ThirdParty/ProtocolBuffers2/objectivec:ProtocolBuffers_lib")))
        .add(attr("$xcodegen", LABEL).cfg(HOST).exec()
            .value(env.getLabel("//tools/objc:xcodegen")))
        .build();
  }
}

/*<!-- #BLAZE_RULE (NAME = objc_proto_library, TYPE = LIBRARY, FAMILY = Objective-C) -->

${ATTRIBUTE_SIGNATURE}

<p>This rule produces a static library from the given proto_library dependencies, after applying an
options file.</p>

${ATTRIBUTE_DEFINITION}

<!-- #END_BLAZE_RULE -->*/
