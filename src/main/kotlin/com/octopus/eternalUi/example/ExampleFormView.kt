package com.octopus.eternalUi.example

 import com.octopus.eternalUi.domain.*

 import com.octopus.eternalUi.domain.db.ListDataProvider
 import com.octopus.eternalUi.vaadinBridge.EternalUI
 import com.vaadin.flow.component.dependency.JsModule
 import com.vaadin.flow.router.Route
 import com.vaadin.flow.spring.annotation.UIScope
 import org.springframework.beans.factory.annotation.Autowired
 import org.springframework.core.io.ClassPathResource
 import org.springframework.stereotype.Component
 import org.springframework.stereotype.Service
 import java.io.InputStream
 import java.util.*
 import java.util.function.BiFunction

@Route("exampleUI")
@UIScope
@JsModule("./example-style.js")
class ExampleFormView(@Autowired var exampleForm: ExampleForm): EternalUI(exampleForm)

@Component
@UIScope
class ExampleForm: Page(
        VerticalContainer(
                Label("Example Eternal UI Application", "h1"),
                Label("A simple UI with a grid, and a CRUD on the grid entity", "h3"),
                Input("name"),
                Grid("listBasedGrid", GridBean::class, listOf("name", "surname")),
                Button("addElementToGrid", caption = "Example Button that add a bean to the grid"),
                Button("openDialog", caption = "Example Button that open a dialog"),
                Button("openDialogWithValues", caption = "Example Button that open a dialog With starting values"),
                Button("openConfirmDialog", caption = "Example Button that open a confirm dialog"),
                Button("navigateToHome", caption = "Example Button to navigate to Home"),
                DownloadButton("downloadCsv")),
        pageDomain = ExampleFormDomain()) {

    val listDataProvider: ListDataProvider = ListDataProvider(BiFunction { v, filters ->  !filters.containsKey("name") ||
            (v as GridBean).name.toLowerCase().contains(filters["name"].toString().toLowerCase())},
            GridBean("Marco", "Manzi"), GridBean("Francesco", "Manzi"))
    @Autowired lateinit var entityFormOnlyForEntity: ExampleFormOnlyForEntity

    fun listBasedGridDataProvider() = UiDataProvider.definition(listDataProvider, "name")

    fun openDialogClicked(exampleFormDomain: ExampleFormDomain) = exampleFormDomain.apply {
        EternalUI.showInUI(ModalWindow(entityFormOnlyForEntity.withEntity(ExampleFormOnlyForEntityDomain()), _cssClassName = "exampleDialogCssClass"))
    }

    fun openDialogWithValuesClicked(exampleFormDomain: ExampleFormDomain) = exampleFormDomain.apply {
        EternalUI.showInUI(ModalWindow(entityFormOnlyForEntity.withEntity(ExampleFormOnlyForEntityDomain("Test Start Name"))))
    }

    fun navigateToHomeClicked(exampleFormDomain: ExampleFormDomain): ExampleFormDomain = exampleFormDomain.apply {
        EternalUI.navigateTo(HomeView::class.java)
    }

    fun openConfirmDialogClicked(it: ExampleFormDomain): ExampleFormDomain = it.apply {
        EternalUI.showInUI(ConfirmDialog("This is a test confirm dialog", { EternalUI.showInUI(UserMessage("You clicked ok")) }, { EternalUI.showInUI(UserMessage("You clicked cancel")) }))
    }

    fun addElementToGridClicked(it: EternalUI): EternalUI = it.refreshItemsAfterAction("listBasedGrid") {
        listDataProvider.elements.add(GridBean("New" + UUID.randomUUID().toString(), "Surname"))
    }

    fun downloadCsvClicked(it: ExampleFormDomain): Pair<(ExampleFormDomain) -> String, (ExampleFormDomain) -> InputStream> =
            Pair<(ExampleFormDomain) -> String, (ExampleFormDomain) -> InputStream>({ "test.csv"}, { ClassPathResource("testFile.csv").inputStream })
}

data class ExampleFormDomain(val name: String = "")

data class GridBean(val name: String, val surname: String)