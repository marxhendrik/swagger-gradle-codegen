/**
 * NOTE: This class is auto generated by the Swagger Gradle Codegen for the following API: JUnit Tests
 *
 * More info on this tool is available on https://github.com/Yelp/swagger-gradle-codegen
 */

package com.yelp.codegen.generatecodesamples.models

import com.squareup.moshi.Json
import com.yelp.codegen.generatecodesamples.tools.XNullable
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

/**
 *
 * @property dateProperty
 * @property datetimeProperty
 * @property doubleProperty
 */
data class XnullableFormatResponses(
    @Json(name = "date_property") @field:Json(name = "date_property") @XNullable var dateProperty: LocalDate? = null,
    @Json(name = "datetime_property") @field:Json(name = "datetime_property") @XNullable var datetimeProperty: ZonedDateTime? = null,
    @Json(name = "double_property") @field:Json(name = "double_property") @XNullable var doubleProperty: Double? = null
)
