package demo;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Henri Kerola / Vaadin
 */
@SpringUI
@Theme("valo")
public class TodoUI extends UI {

    @Autowired
    TodoRepository repository;
    private Grid todosGrid;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        Label header = new Label("TODOs");
        header.setSizeUndefined();
        header.addStyleName(ValoTheme.LABEL_H1);
        layout.addComponent(header);
        layout.setComponentAlignment(header, Alignment.MIDDLE_CENTER);

        HorizontalLayout noteLayout = new HorizontalLayout();
        noteLayout.setWidth("80%");
        noteLayout.setSpacing(true);
        layout.addComponent(noteLayout);
        layout.setComponentAlignment(noteLayout, Alignment.MIDDLE_CENTER);

        TextField noteField = new TextField();
        noteField.setWidth("100%");
        noteLayout.addComponent(noteField);
        noteLayout.setExpandRatio(noteField, 1);

        Button saveButton = new Button("", e -> {
            repository.saveAndFlush(new Todo(noteField.getValue()));
            refreshGrid();
            noteField.clear();
            e.getButton().setEnabled(false);
        });
        saveButton.setIcon(FontAwesome.PLUS);
        saveButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        noteField.addTextChangeListener(e -> saveButton.setEnabled(!e.getText().isEmpty()));
        saveButton.setEnabled(false);

        noteLayout.addComponent(saveButton);

        todosGrid = new Grid();
        todosGrid.setSelectionMode(Grid.SelectionMode.NONE);
        todosGrid.setWidth("80%");
        todosGrid.setHeight("100%");
        todosGrid.addItemClickListener(e -> {
            if (e.getPropertyId().equals("done")) {
                Todo note = (Todo) e.getItemId();
                note.toggleDone();
                repository.saveAndFlush(note);

                refreshGrid();
            }
        });

        layout.addComponent(todosGrid);
        layout.setComponentAlignment(todosGrid, Alignment.MIDDLE_CENTER);
        layout.setExpandRatio(todosGrid, 1);

        refreshGrid();

    }

    private void refreshGrid() {
        List<Todo> todos = repository.findAll();

        todosGrid.setContainerDataSource(new BeanItemContainer<>(Todo.class, todos));

        Grid.Column doneColumn = todosGrid.getColumn("done");
        doneColumn.setHeaderCaption("");
        doneColumn.setConverter(new BooleanToFontIconConverter());
        doneColumn.setRenderer(new HtmlRenderer());
        doneColumn.setWidth(40);
    }
}
