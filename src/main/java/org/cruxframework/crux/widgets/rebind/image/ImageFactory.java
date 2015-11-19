/*
 * Copyright 2014 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.widgets.rebind.image;

import org.apache.commons.lang3.StringUtils;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllFocusHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasEnabledFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.LoadErrorEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.LoadEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.widgets.client.image.Image;
import org.cruxframework.crux.widgets.rebind.event.SelectEvtBind;

import com.google.gwt.resources.client.ImageResource;

/**
 *Factory for Image Widgets
 * @authorThiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(library="widgets", id="image", targetWidget=Image.class, 
	description="An image widget that handle select events properly on touch devices.")

@TagAttributes({
	@TagAttribute(value="preventDefaultTouchEvents", type=Boolean.class, defaultValue="false"),
	@TagAttribute(value="url", processor=ImageFactory.URLAttributeParser.class, supportsResources=true),
	@TagAttribute(value="width", description="Sets the object's width, in CSS units (e.g. \"10px\", \"1em\"). This width does not include decorations such as border, margin, and padding."),
	@TagAttribute(value="height", description="Sets the object's height, in CSS units (e.g. \"10px\", \"1em\"). This height does not include decorations such as border, margin, and padding."),
	@TagAttribute(value="altText"),
	@TagAttribute(value="visibleRect", processor=ImageFactory.VisibleRectAttributeParser.class, supportsDataBinding=false)
})	
@TagEvents({
	@TagEvent(LoadEvtBind.class),
	@TagEvent(LoadErrorEvtBind.class),
	@TagEvent(SelectEvtBind.class)
})
public class ImageFactory extends WidgetCreator<WidgetCreatorContext>
						implements HasAllFocusHandlersFactory<WidgetCreatorContext>, HasEnabledFactory<WidgetCreatorContext>
{
	public static class URLAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		public URLAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, final WidgetCreatorContext context, String attributeValue)
        {
			String property = context.readWidgetProperty("url");
	        if (getWidgetCreator().isResourceReference(property))
	        {
	        	String resource = ViewFactoryCreator.createVariableName("resource");
	        	
	        	String height = context.readWidgetProperty("height");
				if(StringUtils.isEmpty(height))
				{
					height = resource+".getHeight()";
				} else
				{
					height = height.replaceAll("[^\\d.]", "");
				}
				
				String width = context.readWidgetProperty("width");
				if(StringUtils.isEmpty(width))
				{
					width = resource+".getWidth()";
				} else
				{
					width = width.replaceAll("[^\\d.]", "");
				}
	        	
	        	out.println("final " + ImageResource.class.getCanonicalName()+" "+resource+" = "+getWidgetCreator().getResourceAccessExpression(property)+";");
	        	
	        	out.println("com.google.gwt.core.client.Scheduler.get().scheduleDeferred(new com.google.gwt.core.client.Scheduler.ScheduledCommand() { @Override public void execute() {");
	        	out.println(context.getWidget() + ".setUrlAndVisibleRect("+
	        			resource + ".getSafeUri().asString(), "+resource+".getLeft(), "+resource+".getTop(), "+height+", "+width+");");
	        	out.println(" } });");
	        }
	        else
	        {
	        	out.println(context.getWidget()+".setUrl("+EscapeUtils.quote(property)+");");
	        }
        }
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VisibleRectAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		public VisibleRectAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
			String widget = context.getWidget();
			String[] coord = attributeValue.split(",");
			
			if (coord != null && coord.length == 4)
			{
				out.println(widget+".setVisibleRect("+Integer.parseInt(coord[0].trim())+", "+Integer.parseInt(coord[1].trim())+","+ 
						Integer.parseInt(coord[2].trim())+", "+Integer.parseInt(coord[3].trim())+");");
			}
        }
	}

	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
