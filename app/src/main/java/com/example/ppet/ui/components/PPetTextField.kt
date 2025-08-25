package com.example.ppet.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ppet.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PPetTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        
        if (label.isNotEmpty()) {
            Text(
                text = label,
                fontFamily = NotoSansKR,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isError) ErrorRed else TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused },
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        fontFamily = NotoSansKR,
                        fontSize = 16.sp,
                        color = TextTertiary
                    )
                }
            },
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isError) ErrorRed else if (isFocused) OrangePrimary else GrayMedium,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingIcon = trailingIcon,
            isError = isError,
            singleLine = singleLine,
            maxLines = maxLines,
            enabled = enabled,
            readOnly = readOnly,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            textStyle = TextStyle(
                fontFamily = NotoSansKR,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                
                focusedBorderColor = if (isError) ErrorRed else OrangePrimary,
                focusedLabelColor = if (isError) ErrorRed else OrangePrimary,
                focusedLeadingIconColor = if (isError) ErrorRed else OrangePrimary,

                
                unfocusedBorderColor = if (isError) ErrorRed else GrayLight,
                unfocusedLabelColor = TextSecondary,
                unfocusedLeadingIconColor = GrayMedium,

                
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = BackgroundGray,

                
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                disabledTextColor = TextTertiary,

                
                cursorColor = OrangePrimary,

                
                errorBorderColor = ErrorRed,
                errorLabelColor = ErrorRed,
                errorLeadingIconColor = ErrorRed,
                errorTrailingIconColor = ErrorRed,
                errorCursorColor = ErrorRed
            )
        )

        
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                fontFamily = NotoSansKR,
                fontSize = 12.sp,
                color = ErrorRed,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
fun PPetTextFieldMultiline(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    minLines: Int = 3,
    maxLines: Int = 5,
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    PPetTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        isError = isError,
        errorMessage = errorMessage,
        singleLine = false,
        maxLines = maxLines,
        enabled = enabled,
        keyboardOptions = keyboardOptions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PPetDropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    placeholder: String = "선택하세요",
    leadingIcon: ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        
        if (label.isNotEmpty()) {
            Text(
                text = label,
                fontFamily = NotoSansKR,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isError) ErrorRed else TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded && enabled }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                placeholder = {
                    Text(
                        text = placeholder,
                        fontFamily = NotoSansKR,
                        fontSize = 16.sp,
                        color = TextTertiary
                    )
                },
                leadingIcon = leadingIcon?.let { icon ->
                    {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isError) ErrorRed else if (expanded) OrangePrimary else GrayMedium,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                isError = isError,
                enabled = enabled,
                textStyle = TextStyle(
                    fontFamily = NotoSansKR,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isError) ErrorRed else OrangePrimary,
                    unfocusedBorderColor = if (isError) ErrorRed else GrayLight,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = BackgroundGray,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    disabledTextColor = TextTertiary,
                    errorBorderColor = ErrorRed
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .exposedDropdownSize()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontFamily = NotoSansKR,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                        },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = TextPrimary,
                            leadingIconColor = OrangePrimary,
                            trailingIconColor = OrangePrimary
                        )
                    )
                }
            }
        }

        
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                fontFamily = NotoSansKR,
                fontSize = 12.sp,
                color = ErrorRed,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}
