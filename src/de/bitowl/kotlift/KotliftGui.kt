package de.bitowl.kotlift

import com.moshbit.kotlift.Replacement
import com.moshbit.kotlift.Transpiler
import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class KotliftGui : Application() {

  private lateinit var kotlinCode: TextArea
  private lateinit var swiftCode: TextArea
  private val transpiler = Transpiler(listOf(
      Replacement("val", "let", true),
      Replacement("null", "nil", true),
      Replacement("this", "self", true),
      Replacement("Unit", "Void", true),
      Replacement("Boolean", "Bool", true),
      Replacement("Float", "Double", true),
      Replacement("Long", "Int64", true),
      Replacement("Int", "Int32", true),
      Replacement("Short", "Int16", true),
      Replacement("Byte", "Int8", true),
      Replacement("println", "print", true),
      Replacement("print\\(\\)", "print(\"\")", true),
      Replacement("interface", "protocol", true),
      Replacement("get\\(\\)", "get", true),
      Replacement("protected", "internal", true),
      Replacement("Collection", "Array", true),
      Replacement(".invoke", "", true)
  ))

  override fun start(stage: Stage) {
    stage.title = "Kotlift"
    
    val kotlin = Label("Kotlin Code")
    kotlinCode = TextArea()
    kotlinCode.promptText = "Kotlin Code"

    val swift = Label("Swift Code")
    swiftCode = TextArea()
    swiftCode.editableProperty()?.set(false)
    swiftCode.promptText = "Swift Code"
    swiftCode.setOnMouseClicked {
      swiftCode.selectAll()
    }

    val convertButton = Button("Convert")
    convertButton.setOnMouseClicked {
      convertCode()
    }

    val pane = VBox(VBox(kotlin, kotlinCode), convertButton, VBox(swift, swiftCode))
    pane.spacing = 5.0
    pane.padding = Insets(10.0, 10.0, 10.0, 10.0)

    val scene = Scene(pane, 800.0, 600.0)
    stage.scene = scene
    stage.show()
  }

  private fun convertCode() {
    val code = kotlinCode.text?.split("\n") ?: return
    try {
      val output = transpiler.transpile(code)
      var text = ""
      for (line in output) {
        text += line + "\n"
      }
      swiftCode.text = text
    } catch (e: Exception) {
      val alert = Alert(Alert.AlertType.ERROR, e.message)
      alert.show()
    }


  }

  companion object {

    @JvmStatic
    fun main(args: Array<String>) {
      launch(KotliftGui::class.java, *args)
    }
  }
}
