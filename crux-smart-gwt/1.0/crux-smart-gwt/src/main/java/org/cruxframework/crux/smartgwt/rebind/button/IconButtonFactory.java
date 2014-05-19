package org.cruxframework.crux.smartgwt.rebind.button;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.button.IconButton;

/**
 * Factory for IconButton SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="iconButton", targetWidget=IconButton.class)

@TagAttributes({
	@TagAttribute("title"),
	@TagAttribute("baseStyle"),
	@TagAttribute("icon"),
	@TagAttribute("iconAlign"),
	@TagAttribute("iconOrientation"),
	@TagAttribute("largeIcon"),
	@TagAttribute("menuIconSrc"),
	@TagAttribute("orientation")
}) 
	
public class IconButtonFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
