/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.reporter.api.jackson;

import static io.gravitee.reporter.api.configuration.Rules.FIELD_WILDCARD;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class FieldPropertyFilter extends SimpleBeanPropertyFilter {

    private static final String JSON_NESTED_SEPARATOR = ".";
    private final Map<String, Boolean> cache;

    private final Map<String, String> renameFields;
    private final Set<String> excludeExpressions;
    private final Set<String> includeExpressions;
    private final boolean shouldApplyFilter;

    /**
     * Creates a property filter that will include or exclude json properties based on provided <code>rules</code>.
     */
    public FieldPropertyFilter(
        final Map<String, String> renameFields,
        final Set<String> includeExpressions,
        final Set<String> excludeExpressions
    ) {
        this.renameFields = renameFields;
        this.includeExpressions = includeExpressions;
        this.excludeExpressions = excludeExpressions;
        this.cache = new ConcurrentHashMap<>();
        this.shouldApplyFilter = this.shouldApplyFilter();
    }

    private boolean shouldApplyFilter() {
        return (
            (renameFields != null && !renameFields.isEmpty()) ||
            (includeExpressions != null && !includeExpressions.isEmpty()) ||
            (excludeExpressions != null && !excludeExpressions.isEmpty())
        );
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (!shouldApplyFilter) {
            super.serializeAsField(pojo, jgen, provider, writer);
        } else {
            String jsonPath = JacksonUtils.resolveJsonPath(jgen.getOutputContext(), writer);

            if (cache.computeIfAbsent(jsonPath, k -> include(jgen, writer))) {
                // Check that the field to include has to be renamed
                final JsonGenerator generator = (renameFields.containsKey(jsonPath))
                    ? new RenameFieldJsonGenerator(jgen, renameFields.get(jsonPath))
                    : jgen;

                // Only serialize field if included.
                super.serializeAsField(pojo, generator, provider, writer);
            }
        }
    }

    /**
     * Indicate of the property must be included or not.
     * This function first resolve the complete json path of the property and then try to identify if the property must be included or excluded
     * according the filter expression.
     * <p>
     * Note : when <code>includedExpression</code> is empty, it means all properties are considered to be included by default.
     *
     * @see #matchInclude(String, String, PropertyWriter)
     * @see #matchExclude(String, String)
     */
    private boolean include(JsonGenerator jgen, PropertyWriter writer) {
        String jsonPath = JacksonUtils.resolveJsonPath(jgen.getOutputContext(), writer);

        return (
            !(excludeExpressions.contains(FIELD_WILDCARD) || excludeExpressions.stream().anyMatch(s -> matchExclude(jsonPath, s))) ||
            (includeExpressions.stream().anyMatch(e -> matchInclude(jsonPath, e, writer)))
        );
    }

    /**
     * Field must be included if one of the following rules is <code>true</code> :
     * <ul>
     * <li>the field matches the expression</li>
     * <li>the field starts with the expression + 'DOT'.
     * Means that the field is included by the expression cause it is a nested field.
     * Ex: expression 'a' must include all nested fields such as 'a.field'</li>
     * <li>the field is a structure and the expression starts with the json path.
     * Means the json path is an enclosing field that may contains sub fields to include.
     * Ex: expression 'a.field' must include 'a' to be able to process the field 'a.field' later</li>
     * </ul>
     */
    private boolean matchInclude(String jsonPath, String expression, PropertyWriter writer) {
        return (
            jsonPath.equals(expression) ||
            jsonPath.startsWith(expression + JSON_NESTED_SEPARATOR) ||
            (isAStructure(writer) && expression.startsWith(jsonPath + JSON_NESTED_SEPARATOR))
        );
    }

    /**
     * Field must be excluded if one of the following rules is <code>true</code> :
     * <ul>
     * <li>the field matches the expression</li>
     * <li>the field starts with the expression + 'DOT'. Means that the field is included by the expression cause it is a nested field.
     * Ex: expression 'a' must exclude all nested fields such as 'a.field'</li>
     * </ul>
     */
    private boolean matchExclude(String jsonPath, String expression) {
        return jsonPath.equals(expression) || jsonPath.startsWith(expression + JSON_NESTED_SEPARATOR);
    }

    private boolean isAStructure(PropertyWriter writer) {
        return !writer.getType().isPrimitive() || writer.getType().isContainerType();
    }

    public static class RenameFieldJsonGenerator extends JsonGenerator {

        private final JsonGenerator wrapped;
        private final String fieldName;

        public RenameFieldJsonGenerator(JsonGenerator wrapped, String fieldName) {
            this.wrapped = wrapped;
            this.fieldName = fieldName;
        }

        @Override
        public void writeFieldName(String name) throws IOException {
            wrapped.writeFieldName(fieldName);
        }

        @Override
        public void writeFieldName(SerializableString name) throws IOException {
            wrapped.writeFieldName(new SerializedString(fieldName));
        }

        @Override
        public JsonGenerator setCodec(ObjectCodec oc) {
            return wrapped.setCodec(oc);
        }

        @Override
        public ObjectCodec getCodec() {
            return wrapped.getCodec();
        }

        @Override
        public Version version() {
            return wrapped.version();
        }

        @Override
        public JsonGenerator enable(Feature f) {
            return wrapped.enable(f);
        }

        @Override
        public JsonGenerator disable(Feature f) {
            return wrapped.disable(f);
        }

        @Override
        public boolean isEnabled(Feature f) {
            return wrapped.isEnabled(f);
        }

        @Override
        public boolean isEnabled(StreamWriteFeature f) {
            return wrapped.isEnabled(f);
        }

        @Override
        public int getFeatureMask() {
            return wrapped.getFeatureMask();
        }

        @Override
        @Deprecated
        public JsonGenerator setFeatureMask(int values) {
            return wrapped.setFeatureMask(values);
        }

        @Override
        public JsonGenerator overrideStdFeatures(int values, int mask) {
            return wrapped.overrideStdFeatures(values, mask);
        }

        @Override
        public int getFormatFeatures() {
            return wrapped.getFormatFeatures();
        }

        @Override
        public JsonGenerator overrideFormatFeatures(int values, int mask) {
            return wrapped.overrideFormatFeatures(values, mask);
        }

        @Override
        public void setSchema(FormatSchema schema) {
            wrapped.setSchema(schema);
        }

        @Override
        public FormatSchema getSchema() {
            return wrapped.getSchema();
        }

        @Override
        public JsonGenerator setPrettyPrinter(PrettyPrinter pp) {
            return wrapped.setPrettyPrinter(pp);
        }

        @Override
        public PrettyPrinter getPrettyPrinter() {
            return wrapped.getPrettyPrinter();
        }

        @Override
        public JsonGenerator useDefaultPrettyPrinter() {
            return wrapped.useDefaultPrettyPrinter();
        }

        @Override
        public JsonGenerator setHighestNonEscapedChar(int charCode) {
            return wrapped.setHighestNonEscapedChar(charCode);
        }

        @Override
        public int getHighestEscapedChar() {
            return wrapped.getHighestEscapedChar();
        }

        @Override
        public CharacterEscapes getCharacterEscapes() {
            return wrapped.getCharacterEscapes();
        }

        @Override
        public JsonGenerator setCharacterEscapes(CharacterEscapes esc) {
            return wrapped.setCharacterEscapes(esc);
        }

        @Override
        public JsonGenerator setRootValueSeparator(SerializableString sep) {
            return wrapped.setRootValueSeparator(sep);
        }

        @Override
        public Object getOutputTarget() {
            return wrapped.getOutputTarget();
        }

        @Override
        public int getOutputBuffered() {
            return wrapped.getOutputBuffered();
        }

        @Override
        public Object getCurrentValue() {
            return wrapped.getCurrentValue();
        }

        @Override
        public void setCurrentValue(Object v) {
            wrapped.setCurrentValue(v);
        }

        @Override
        public boolean canUseSchema(FormatSchema schema) {
            return wrapped.canUseSchema(schema);
        }

        @Override
        public boolean canWriteObjectId() {
            return wrapped.canWriteObjectId();
        }

        @Override
        public boolean canWriteTypeId() {
            return wrapped.canWriteTypeId();
        }

        @Override
        public boolean canWriteBinaryNatively() {
            return wrapped.canWriteBinaryNatively();
        }

        @Override
        public boolean canOmitFields() {
            return wrapped.canOmitFields();
        }

        @Override
        public boolean canWriteFormattedNumbers() {
            return wrapped.canWriteFormattedNumbers();
        }

        @Override
        public void writeStartArray() throws IOException {
            wrapped.writeStartArray();
        }

        @Override
        public void writeStartArray(int size) throws IOException {
            wrapped.writeStartArray(size);
        }

        @Override
        public void writeStartArray(Object forValue) throws IOException {
            wrapped.writeStartArray(forValue);
        }

        @Override
        public void writeStartArray(Object forValue, int size) throws IOException {
            wrapped.writeStartArray(forValue, size);
        }

        @Override
        public void writeEndArray() throws IOException {
            wrapped.writeEndArray();
        }

        @Override
        public void writeStartObject() throws IOException {
            wrapped.writeStartObject();
        }

        @Override
        public void writeStartObject(Object forValue) throws IOException {
            wrapped.writeStartObject(forValue);
        }

        @Override
        public void writeStartObject(Object forValue, int size) throws IOException {
            wrapped.writeStartObject(forValue, size);
        }

        @Override
        public void writeEndObject() throws IOException {
            wrapped.writeEndObject();
        }

        @Override
        public void writeFieldId(long id) throws IOException {
            wrapped.writeFieldId(id);
        }

        @Override
        public void writeArray(int[] array, int offset, int length) throws IOException {
            wrapped.writeArray(array, offset, length);
        }

        @Override
        public void writeArray(long[] array, int offset, int length) throws IOException {
            wrapped.writeArray(array, offset, length);
        }

        @Override
        public void writeArray(double[] array, int offset, int length) throws IOException {
            wrapped.writeArray(array, offset, length);
        }

        @Override
        public void writeString(String text) throws IOException {
            wrapped.writeString(text);
        }

        @Override
        public void writeString(Reader reader, int len) throws IOException {
            wrapped.writeString(reader, len);
        }

        @Override
        public void writeString(char[] text, int offset, int len) throws IOException {
            wrapped.writeString(text, offset, len);
        }

        @Override
        public void writeString(SerializableString text) throws IOException {
            wrapped.writeString(text);
        }

        @Override
        public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
            wrapped.writeRawUTF8String(text, offset, length);
        }

        @Override
        public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
            wrapped.writeUTF8String(text, offset, length);
        }

        @Override
        public void writeRaw(String text) throws IOException {
            wrapped.writeRaw(text);
        }

        @Override
        public void writeRaw(String text, int offset, int len) throws IOException {
            wrapped.writeRaw(text, offset, len);
        }

        @Override
        public void writeRaw(char[] text, int offset, int len) throws IOException {
            wrapped.writeRaw(text, offset, len);
        }

        @Override
        public void writeRaw(char c) throws IOException {
            wrapped.writeRaw(c);
        }

        @Override
        public void writeRaw(SerializableString raw) throws IOException {
            wrapped.writeRaw(raw);
        }

        @Override
        public void writeRawValue(String text) throws IOException {
            wrapped.writeRawValue(text);
        }

        @Override
        public void writeRawValue(String text, int offset, int len) throws IOException {
            wrapped.writeRawValue(text, offset, len);
        }

        @Override
        public void writeRawValue(char[] text, int offset, int len) throws IOException {
            wrapped.writeRawValue(text, offset, len);
        }

        @Override
        public void writeRawValue(SerializableString raw) throws IOException {
            wrapped.writeRawValue(raw);
        }

        @Override
        public void writeBinary(Base64Variant bv, byte[] data, int offset, int len) throws IOException {
            wrapped.writeBinary(bv, data, offset, len);
        }

        @Override
        public void writeBinary(byte[] data, int offset, int len) throws IOException {
            wrapped.writeBinary(data, offset, len);
        }

        @Override
        public void writeBinary(byte[] data) throws IOException {
            wrapped.writeBinary(data);
        }

        @Override
        public int writeBinary(InputStream data, int dataLength) throws IOException {
            return wrapped.writeBinary(data, dataLength);
        }

        @Override
        public int writeBinary(Base64Variant bv, InputStream data, int dataLength) throws IOException {
            return wrapped.writeBinary(bv, data, dataLength);
        }

        @Override
        public void writeNumber(short v) throws IOException {
            wrapped.writeNumber(v);
        }

        @Override
        public void writeNumber(int v) throws IOException {
            wrapped.writeNumber(v);
        }

        @Override
        public void writeNumber(long v) throws IOException {
            wrapped.writeNumber(v);
        }

        @Override
        public void writeNumber(BigInteger v) throws IOException {
            wrapped.writeNumber(v);
        }

        @Override
        public void writeNumber(double v) throws IOException {
            wrapped.writeNumber(v);
        }

        @Override
        public void writeNumber(float v) throws IOException {
            wrapped.writeNumber(v);
        }

        @Override
        public void writeNumber(BigDecimal v) throws IOException {
            wrapped.writeNumber(v);
        }

        @Override
        public void writeNumber(String encodedValue) throws IOException {
            wrapped.writeNumber(encodedValue);
        }

        @Override
        public void writeBoolean(boolean state) throws IOException {
            wrapped.writeBoolean(state);
        }

        @Override
        public void writeNull() throws IOException {
            wrapped.writeNull();
        }

        @Override
        public void writeEmbeddedObject(Object object) throws IOException {
            wrapped.writeEmbeddedObject(object);
        }

        @Override
        public void writeObjectId(Object id) throws IOException {
            wrapped.writeObjectId(id);
        }

        @Override
        public void writeObjectRef(Object id) throws IOException {
            wrapped.writeObjectRef(id);
        }

        @Override
        public void writeTypeId(Object id) throws IOException {
            wrapped.writeTypeId(id);
        }

        @Override
        public WritableTypeId writeTypePrefix(WritableTypeId typeIdDef) throws IOException {
            return wrapped.writeTypePrefix(typeIdDef);
        }

        @Override
        public WritableTypeId writeTypeSuffix(WritableTypeId typeIdDef) throws IOException {
            return wrapped.writeTypeSuffix(typeIdDef);
        }

        @Override
        public void writeObject(Object pojo) throws IOException {
            wrapped.writeObject(pojo);
        }

        @Override
        public void writeTree(TreeNode rootNode) throws IOException {
            wrapped.writeTree(rootNode);
        }

        @Override
        public void writeStringField(String fieldName, String value) throws IOException {
            wrapped.writeStringField(fieldName, value);
        }

        @Override
        public void writeOmittedField(String fieldName) throws IOException {
            wrapped.writeOmittedField(fieldName);
        }

        @Override
        public void copyCurrentEvent(JsonParser p) throws IOException {
            wrapped.copyCurrentEvent(p);
        }

        @Override
        public void copyCurrentStructure(JsonParser p) throws IOException {
            wrapped.copyCurrentStructure(p);
        }

        @Override
        public JsonStreamContext getOutputContext() {
            return wrapped.getOutputContext();
        }

        @Override
        public void flush() throws IOException {
            wrapped.flush();
        }

        @Override
        public boolean isClosed() {
            return wrapped.isClosed();
        }

        @Override
        public void close() throws IOException {
            wrapped.close();
        }
    }
}
