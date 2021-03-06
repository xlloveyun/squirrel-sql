package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupAccessor;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.SquirreLRSyntaxTextAreaUI;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.SquirrelRSyntaxTextArea;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.action.SquirrelCopyAsRtfAction;
import org.fife.ui.rtextarea.RTextAreaEditorKit;

import javax.swing.*;
import java.util.HashMap;

public class ToolsPopupHandler
{
   private SyntaxPlugin _syntaxPugin;

   ToolsPopupHandler(SyntaxPlugin syntaxPugin)
   {
      _syntaxPugin = syntaxPugin;
   }

   void initToolsPopup(HashMap<String, Object> props, ISQLEntryPanel isqlEntryPanel)
   {
      // Note: SessionInternalFrame and SQLinternalFrame should never provide
      // a ToolsPopupAccessor. Their Tools Popup ist configured in the SyntaxPlugin class
      // with standard Actions from ActionCollection.
      ToolsPopupAccessor tpa = (ToolsPopupAccessor) props.get((ToolsPopupAccessor.class.getName()));

      if(null == tpa)
      {
         return;
      }

      SyntaxPluginResources rsrc = _syntaxPugin.getResources();
      IApplication app = _syntaxPugin.getApplication();

      tpa.addToToolsPopup(SyntaxPlugin.i18n.FIND , new FindAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.FIND_SELECTED , new FindSelectedAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.REPEAT_LAST_FIND , new RepeatLastFindAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.MARK_SELECTED , new MarkSelectedAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.REPLACE , new ReplaceAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.UNMARK , new UnmarkAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.GO_TO_LINE , new GoToLineAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.AUTO_CORR , new ConfigureAutoCorrectAction(app, rsrc, _syntaxPugin));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.DUP_LINE , new DuplicateLineAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.COMMENT , new CommentAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.UNCOMMENT , new UncommentAction(app,rsrc,isqlEntryPanel));

      if (isqlEntryPanel.getTextComponent() instanceof SquirrelRSyntaxTextArea)
      {
         SquirrelRSyntaxTextArea rsEdit = (SquirrelRSyntaxTextArea) isqlEntryPanel.getTextComponent();

         Action toUpperAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaUpperSelectionCaseAction);
         toUpperAction.putValue(Resources.ACCELERATOR_STRING, SquirreLRSyntaxTextAreaUI.RS_ACCELERATOR_STRING_TO_UPPER_CASE);
         tpa.addToToolsPopup(SyntaxPlugin.i18n.TO_UPPER_CASE, toUpperAction);

         Action toLowerAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaLowerSelectionCaseAction);
         toLowerAction.putValue(Resources.ACCELERATOR_STRING, SquirreLRSyntaxTextAreaUI.RS_ACCELERATOR_STRING_TO_LOWER_CASE);
         tpa.addToToolsPopup(SyntaxPlugin.i18n.TO_LOWER_CASE, toLowerAction);
         
         tpa.addToToolsPopup(SyntaxPlugin.i18n.COPY_AS_RTF,_syntaxPugin.getApplication().getActionCollection().get(SquirrelCopyAsRtfAction.class));
      }
   }


   void initToolsPopup(SessionInternalFrame sif, ActionCollection coll)
   {
      ISQLEntryPanel sep = sif.getSQLPanelAPI().getSQLEntryPanel();
      JComponent septc = sep.getTextComponent();

      sif.addToToolsPopUp(SyntaxPlugin.i18n.FIND, coll.get(FindAction.class));
      sif.addToToolsPopUp(SyntaxPlugin.i18n.FIND_SELECTED , coll.get(FindSelectedAction.class));
      sif.addToToolsPopUp(SyntaxPlugin.i18n.REPEAT_LAST_FIND , coll.get(RepeatLastFindAction.class));
      sif.addToToolsPopUp(SyntaxPlugin.i18n.MARK_SELECTED , coll.get(MarkSelectedAction.class));
      sif.addToToolsPopUp(SyntaxPlugin.i18n.REPLACE, coll.get(ReplaceAction.class));
      sif.addToToolsPopUp(SyntaxPlugin.i18n.UNMARK, coll.get(UnmarkAction.class));
      sif.addToToolsPopUp(SyntaxPlugin.i18n.GO_TO_LINE, coll.get(GoToLineAction.class));
      sif.addToToolsPopUp(SyntaxPlugin.i18n.AUTO_CORR, coll.get(ConfigureAutoCorrectAction.class));
      sif.addToToolsPopUp(SyntaxPlugin.i18n.DUP_LINE, coll.get(DuplicateLineAction.class));
      sif.addToToolsPopUp(SyntaxPlugin.i18n.COMMENT, coll.get(CommentAction.class));
      sif.addToToolsPopUp(SyntaxPlugin.i18n.UNCOMMENT, coll.get(UncommentAction.class));

      if (sep.getTextComponent() instanceof SquirrelRSyntaxTextArea)
      {
         SquirrelRSyntaxTextArea rsEdit = (SquirrelRSyntaxTextArea) sep.getTextComponent();

         Action toUpperAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaUpperSelectionCaseAction);
         toUpperAction.putValue(Resources.ACCELERATOR_STRING, SquirreLRSyntaxTextAreaUI.RS_ACCELERATOR_STRING_TO_UPPER_CASE);
         sif.addToToolsPopUp(SyntaxPlugin.i18n.TO_UPPER_CASE, toUpperAction);

         Action toLowerAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaLowerSelectionCaseAction);
         toLowerAction.putValue(Resources.ACCELERATOR_STRING, SquirreLRSyntaxTextAreaUI.RS_ACCELERATOR_STRING_TO_LOWER_CASE);
         sif.addToToolsPopUp(SyntaxPlugin.i18n.TO_LOWER_CASE, toLowerAction);

         sif.addToToolsPopUp(SyntaxPlugin.i18n.COPY_AS_RTF,_syntaxPugin.getApplication().getActionCollection().get(SquirrelCopyAsRtfAction.class));
      }

   }

   void initToolsPopup(SQLInternalFrame sqlInternalFrame, ActionCollection coll)
   {
      sqlInternalFrame.addToToolsPopUp(SyntaxPlugin.i18n.FIND , coll.get(FindAction.class));
      sqlInternalFrame.addToToolsPopUp(SyntaxPlugin.i18n.FIND_SELECTED , coll.get(FindSelectedAction.class));
      sqlInternalFrame.addToToolsPopUp(SyntaxPlugin.i18n.REPEAT_LAST_FIND , coll.get(RepeatLastFindAction.class));
      sqlInternalFrame.addToToolsPopUp(SyntaxPlugin.i18n.MARK_SELECTED , coll.get(MarkSelectedAction.class));
      sqlInternalFrame.addToToolsPopUp(SyntaxPlugin.i18n.REPLACE , coll.get(ReplaceAction.class));
      sqlInternalFrame.addToToolsPopUp(SyntaxPlugin.i18n.UNMARK , coll.get(UnmarkAction.class));
      sqlInternalFrame.addToToolsPopUp(SyntaxPlugin.i18n.GO_TO_LINE , coll.get(GoToLineAction.class));
      sqlInternalFrame.addToToolsPopUp(SyntaxPlugin.i18n.AUTO_CORR , coll.get(ConfigureAutoCorrectAction.class));
      sqlInternalFrame.addToToolsPopUp(SyntaxPlugin.i18n.DUP_LINE , coll.get(DuplicateLineAction.class));
      sqlInternalFrame.addToToolsPopUp(SyntaxPlugin.i18n.COMMENT , coll.get(CommentAction.class));
      sqlInternalFrame.addToToolsPopUp(SyntaxPlugin.i18n.UNCOMMENT , coll.get(UncommentAction.class));

      ISQLPanelAPI sqlPanelAPI = sqlInternalFrame.getSQLPanelAPI();

      if (sqlPanelAPI.getSQLEntryPanel().getTextComponent() instanceof SquirrelRSyntaxTextArea)
      {
         SquirrelRSyntaxTextArea rsEdit = (SquirrelRSyntaxTextArea) sqlPanelAPI.getSQLEntryPanel().getTextComponent();

         Action toUpperAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaUpperSelectionCaseAction);
         toUpperAction.putValue(Resources.ACCELERATOR_STRING, SquirreLRSyntaxTextAreaUI.RS_ACCELERATOR_STRING_TO_UPPER_CASE);
         sqlInternalFrame.addToToolsPopUp(SyntaxPlugin.i18n.TO_UPPER_CASE, toUpperAction);

         Action toLowerAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaLowerSelectionCaseAction);
         toLowerAction.putValue(Resources.ACCELERATOR_STRING, SquirreLRSyntaxTextAreaUI.RS_ACCELERATOR_STRING_TO_LOWER_CASE);
         sqlInternalFrame.addToToolsPopUp(SyntaxPlugin.i18n.TO_LOWER_CASE, toLowerAction);

         sqlInternalFrame.addToToolsPopUp(SyntaxPlugin.i18n.COPY_AS_RTF,_syntaxPugin.getApplication().getActionCollection().get(SquirrelCopyAsRtfAction.class));
      }
   }


}
