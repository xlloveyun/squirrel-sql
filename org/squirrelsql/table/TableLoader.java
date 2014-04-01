package org.squirrelsql.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class TableLoader
{
   private static final SimpleObjectProperty NULL_PROPERTY = new SimpleObjectProperty("<null>");
   private ArrayList<ColumnHandle> _columns = new ArrayList<>();

   private ArrayList<ArrayList<SimpleObjectProperty>> _rows = new ArrayList<>();

   public ColumnHandle addColumn(String header)
   {
      ColumnHandle columnHandle = new ColumnHandle(header);
      _columns.add(columnHandle);
      return columnHandle;
   }

   public void addRow(List row)
   {
      addRow(row.toArray(new Object[row.size()]));
   }

   public void addRow(Object... row)
   {
      ArrayList<SimpleObjectProperty> buf = new ArrayList<>();

      for (Object o : row)
      {
         buf.add(new SimpleObjectProperty(o));
      }

      _rows.add(buf);
   }

   public void load(TableView tv)
   {
      ArrayList<TableColumn> cols = new ArrayList<>();

      for (int i = 0; i < _columns.size(); i++)
      {
         ColumnHandle columnHandle = _columns.get(i);
         TableColumn tableColumn = new TableColumn(columnHandle.getHeader());

         if(0 < columnHandle.getSelectableValues().length)
         {
            tableColumn.setCellFactory(new Callback<TableColumn, TableCell>()
            {
               @Override
               public TableCell call(TableColumn param)
               {
                  return new ComboBoxTableCell(columnHandle.getSelectableValues());
               }
            });

            tableColumn.setEditable(true);
            tv.setEditable(true);
         }


         final int finalI = i;
         tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ArrayList<SimpleObjectProperty>, Object>, ObservableValue<Object>>()
         {
            public ObservableValue<Object> call(TableColumn.CellDataFeatures<ArrayList<SimpleObjectProperty>, Object> row)
            {
               return getCellValue(finalI, row.getValue());
            }
         });
         cols.add(tableColumn);
      }

      tv.getColumns().setAll(cols);

      ObservableList<ArrayList<SimpleObjectProperty>> items = FXCollections.observableArrayList(_rows);
      tv.setItems(items);

   }

   private SimpleObjectProperty getCellValue(int ix, ArrayList<SimpleObjectProperty> row)
   {
      if (row.size() > ix)
      {
         return interpretValue(row.get(ix));
      }
      else
      {
         return NULL_PROPERTY;
      }
   }

   private SimpleObjectProperty interpretValue(SimpleObjectProperty simpleObjectProperty)
   {
      if(null == simpleObjectProperty.get())
      {
         return NULL_PROPERTY;
      }

      return simpleObjectProperty;
   }

   public ArrayList<String> getCellsAsString(int col)
   {
      ArrayList<String> ret = new ArrayList<>(_rows.size());
      for (ArrayList<SimpleObjectProperty> row : _rows)
      {
         SimpleObjectProperty simpleObjectProperty = interpretValue(row.get(col));
         ret.add(getStringValue(simpleObjectProperty));
      }

      return ret;
   }

   private String getStringValue(SimpleObjectProperty simpleObjectProperty)
   {
      if(simpleObjectProperty.get() instanceof String)
      {
         return (String) simpleObjectProperty.get();
      }

      return simpleObjectProperty.get().toString();
   }

   public String getCellAsString(String columnName, int rowIx)
   {
      return getCellAt(rowIx, getColIxByName(columnName));

   }

   private int getColIxByName(String columnName)
   {
      int colIx = -1;
      for (int i = 0; i < _columns.size(); i++)
      {
         if(_columns.get(i).getHeader().equalsIgnoreCase(columnName))
         {
            colIx = i;
            break;
         }
      }

      if (-1 == colIx)
      {
         throw new IllegalArgumentException("Unknown column name: " + columnName);
      }
      return colIx;
   }

   public int getCellAsInt(String columnName, int rowIx)
   {
      return (int) _rows.get(rowIx).get(getColIxByName(columnName)).get();
   }

   public Integer getCellAsInteger(String columnName, int rowIx)
   {
      return (Integer) _rows.get(rowIx).get(getColIxByName(columnName)).get();
   }


   private String getCellAt(int rowIx, int colIx)
   {
      SimpleObjectProperty simpleObjectProperty = getObjectPropertyAt(rowIx, colIx);
      return getStringValue(simpleObjectProperty);
   }

   private SimpleObjectProperty getObjectPropertyAt(int rowIx, int colIx)
   {
      return interpretValue(_rows.get(rowIx).get(colIx));
   }

   public int size()
   {
      return _rows.size();
   }

   public ArrayList<ArrayList> getRows()
   {
      ArrayList<ArrayList> ret = new ArrayList<>();

      for (int i = 0; i < size(); i++)
      {
         ArrayList row = new ArrayList();
         for (int j = 0; j < _columns.size(); j++)
         {
            ArrayList<SimpleObjectProperty> sopRow = _rows.get(i);
            if (j < sopRow.size())
            {
               SimpleObjectProperty simpleObjectProperty = sopRow.get(j);
               row.add(simpleObjectProperty.get());
            }
            else
            {
               row.add(null);
            }
         }

         ret.add(row);
      }

      return ret;
   }

   public void addRows(ArrayList<ArrayList> rows)
   {
      for (ArrayList row : rows)
      {
         addRow(row);
      }
   }

   public void clearRows()
   {
      _rows.clear();
   }

   public ArrayList<ArrayList<SimpleObjectProperty>> getSimpleObjectPropertyRows()
   {
      return _rows;
   }
}
