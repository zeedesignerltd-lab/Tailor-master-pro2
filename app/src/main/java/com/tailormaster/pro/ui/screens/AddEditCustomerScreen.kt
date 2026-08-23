package com.tailormaster.pro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tailormaster.pro.data.Customer
import com.tailormaster.pro.ui.components.SectionLabel
import com.tailormaster.pro.viewmodel.CustomerViewModel
import kotlinx.coroutines.launch

@Composable
fun AddEditCustomerScreen(
    vm: CustomerViewModel,
    editingId: String?,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var existing by remember { mutableStateOf<Customer?>(null) }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var shoulder by remember { mutableStateOf("") }
    var chest by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    var hip by remember { mutableStateOf("") }
    var neck by remember { mutableStateOf("") }
    var sleeve by remember { mutableStateOf("") }
    var shirtLength by remember { mutableStateOf("") }
    var shalwarLength by remember { mutableStateOf("") }
    var bottomWidth by remember { mutableStateOf("") }
    var cuff by remember { mutableStateOf("") }
    var pocket by remember { mutableStateOf("") }
    var collar by remember { mutableStateOf("") }

    LaunchedEffect(editingId) {
        if (editingId != null) {
            val c = vm.getById(editingId)
            existing = c
            c?.let {
                name = it.name; phone = it.phone; address = it.address; notes = it.notes
                shoulder = it.shoulder; chest = it.chest; waist = it.waist; hip = it.hip
                neck = it.neck; sleeve = it.sleeve; shirtLength = it.shirtLength
                shalwarLength = it.shalwarLength; bottomWidth = it.bottomWidth
                cuff = it.cuff; pocket = it.pocket; collar = it.collar
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            if (editingId == null) "Add Customer" else "Edit Customer",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(16.dp))

        LabeledField("Customer Name", name) { name = it }
        LabeledField("Phone Number", phone) { phone = it }
        LabeledField("Address", address) { address = it }
        LabeledField("Notes", notes) { notes = it }

        SectionLabel("Measurements")
        val measureFields = listOf(
            "Shoulder" to (shoulder to { v: String -> shoulder = v }),
            "Chest" to (chest to { v: String -> chest = v }),
            "Waist" to (waist to { v: String -> waist = v }),
            "Hip" to (hip to { v: String -> hip = v }),
            "Neck" to (neck to { v: String -> neck = v }),
            "Sleeve" to (sleeve to { v: String -> sleeve = v }),
            "Shirt Length" to (shirtLength to { v: String -> shirtLength = v }),
            "Shalwar Length" to (shalwarLength to { v: String -> shalwarLength = v }),
            "Bottom Width" to (bottomWidth to { v: String -> bottomWidth = v }),
            "Cuff" to (cuff to { v: String -> cuff = v }),
            "Pocket" to (pocket to { v: String -> pocket = v }),
            "Collar" to (collar to { v: String -> collar = v })
        )

        measureFields.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                pair.forEach { (label, pairVal) ->
                    val (value, setter) = pairVal
                    OutlinedTextField(
                        value = value,
                        onValueChange = setter,
                        label = { Text(label) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                scope.launch {
                    if (editingId == null) {
                        val code = vm.generateCode(name.ifBlank { "Customer" })
                        vm.save(
                            Customer(
                                customerCode = code, name = name, phone = phone, address = address,
                                notes = notes, shoulder = shoulder, chest = chest, waist = waist,
                                hip = hip, neck = neck, sleeve = sleeve, shirtLength = shirtLength,
                                shalwarLength = shalwarLength, bottomWidth = bottomWidth,
                                cuff = cuff, pocket = pocket, collar = collar
                            )
                        ) { onSaved() }
                    } else {
                        existing?.let {
                            vm.update(
                                it.copy(
                                    name = name, phone = phone, address = address, notes = notes,
                                    shoulder = shoulder, chest = chest, waist = waist, hip = hip,
                                    neck = neck, sleeve = sleeve, shirtLength = shirtLength,
                                    shalwarLength = shalwarLength, bottomWidth = bottomWidth,
                                    cuff = cuff, pocket = pocket, collar = collar
                                )
                            ) { onSaved() }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Save")
        }
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun LabeledField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    )
}
