/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author user
 */
public class Dashboard extends javax.swing.JFrame {

    private static Connection conn;
    private static Statement stat;
    private final AbstractBorder circleBorder = new CircleBorder();
    public boolean a = true;
    public int bulan=0;
    String userLevel;
    String userName;
    
    public Dashboard() {
        initComponents();
        setLayout();
        Koneksi DB = new Koneksi();
        DB.config();
        conn = DB.conn;
        stat = DB.stat;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
    }
    
    public Dashboard(String nama, String level, byte[] img){
        initComponents();
        setLayout();
        Koneksi DB = new Koneksi();
        DB.config();
        conn = DB.conn;
        stat = DB.stat;
        this.userLevel=level;
        this.userName=nama;
        lbl_name.setText(nama);
        lbl_level.setText(level);
        ptgsName.setText(nama+" !");
        admName.setText(nama+" !");
        ptgsName1.setText(nama+" !");
        imgUser.setIcon(new ImageIcon(new ImageIcon(img).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
        dashActive();
    }
    
    private void setLayout(){
        this.setIconImage(new ImageIcon(getClass().getResource("/img/storeset.png")).getImage());
        this.setBackground(new Color(0,0,0,0));        
        this.setExtendedState(MAXIMIZED_BOTH);
        fullScreen(bg);
        setCircleImg(imgUser);
        setCircleImg(uImg);
        setJDateChooserFormat(xtgl1);
        setJDateChooserFormat(xtgl2);
        setJDateChooserFormat(tgl3);
        setJDateChooserFormat(tgl4);
        getYear(tahun);
        getYear(tahun1);
        iuname.setIcon(null);
        ipass.setIcon(null);
        txtkode_temp.hide();
    }
    
    public void getYear(JLabel lb){
        Date d = new Date();
        LocalDate ld = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = ld.getYear();
        lb.setText(String.valueOf(year));
    }
    
    public void setJDateChooserFormat(JDateChooser jd){
        Border borderz = BorderFactory.createEmptyBorder();
        ((JTextFieldDateEditor)jd.getDateEditor()).setBorder(borderz);
        ((JTextFieldDateEditor)jd.getDateEditor()).setFont(new Font("SansSerif", Font.PLAIN, 14));
        ((JTextFieldDateEditor)jd.getDateEditor()).setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void setCircleImg(JLabel lb){
        lb.setBorder(circleBorder); 
        lb.setForeground(new Color (0,0,0,0));
        lb.setBackground(new Color (0,0,0,0));        
    }
    
    private void fullScreen(JPanel panel){
        Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
        panel.setSize(sz.width, sz.height);
    }
    
    private void fadeIn(JFrame fm){
        for (double i=0.0; i<=1.0; i=i+0.1){
            String val = i + "";
            float f = Float.valueOf(val);
            fm.setOpacity(f);
            try{
                Thread.sleep(20);
            }catch (InterruptedException e){
                e.getMessage();
            }
        }        
    }
   
    private void fadeOut(JFrame fm){
        for (double i=1.0; i>=0.0; i=i-0.1){
            String val = i + "";
            float f = Float.valueOf(val);
            fm.setOpacity(f);
            try{
                Thread.sleep(20);
            }catch (InterruptedException e){ 
                e.getMessage();
            }
        }
    }
    
   private void dashActive(){
        setColor(bDash,act1,ldash);
        resetColor(bMaster,act2,lmaster);
        resetColor(bTrans,act3,ltrans);
        resetColor(bLap,act4,llap);
        resetColor(bMUser,act5,lmuser);
        resetColor(bKeluar,act6,lkeluar);
        setIcon(ldash, "/img/home_b.png");
        setIcon(lmaster, "/img/master_g.png");
        setIcon(ltrans, "/img/trans_g.png");
        setIcon(llap, "/img/report_g.png");
        setIcon(lmuser, "/img/mnguser_g.png");
        setIcon(lkeluar, "/img/exit_g.png");  
        if(null != userLevel)switch (userLevel) {
            case "Admin":
                dashAdmin.setVisible(true);
                setPanelVisible(dashGudang, dashKasir, master, master_barang, master_pelanggan, master_supplier, false);
                setPanelVisible(master_merk, master_satuan, master_pegawai, trans, lap, mngUser, false);
                getDateTime();
                getSummary();
                break;
            case "Staf Gudang":
                dashGudang.setVisible(true);
                dataStok();
                setPanelVisible(dashAdmin, dashKasir, master, master_barang, master_pelanggan, master_supplier, false);
                setPanelVisible(master_merk, master_satuan, master_pegawai, trans, lap, mngUser, false);
                break;
            case "Staf Kasir":            
                dashKasir.setVisible(true);
                setPanelVisible(dashGudang, dashAdmin, master, master_barang, master_pelanggan, master_supplier, false);
                setPanelVisible(master_merk, master_satuan, master_pegawai, trans, lap, mngUser, false);
                break;
            default:
                break;
        } 
    }
    
    private void bMasterPressed(){
        setColor(bMaster,act2,lmaster);
        resetColor(bDash,act1,ldash);
        resetColor(bTrans,act3,ltrans);
        resetColor(bLap,act4,llap);
        resetColor(bMUser,act5,lmuser);
        resetColor(bKeluar,act6,lkeluar);
        setIcon(lmaster, "/img/master_b.png");
        setIcon(ldash, "/img/home_g.png");
        setIcon(ltrans, "/img/trans_g.png");  
        setIcon(llap, "/img/report_g.png");
        setIcon(lmuser, "/img/mnguser_g.png"); 
        setIcon(lkeluar, "/img/exit_g.png");
        master.setVisible(true);
        setPanelVisible(dashGudang, dashKasir, dashAdmin, master_barang, master_pelanggan, master_supplier, false);
        setPanelVisible(master_merk, master_satuan, master_pegawai, trans, lap, mngUser, false);
    }
    
   public static void dataBarang(){
        // membuat tampilan model tabel
        DefaultTableModel model = new DefaultTableModel(){
            public Class getColumnClass(int column, int row){
                Class returnValue;
		if (row!=0){
                    row=0;
		}
                if ((column >=0) && (column < getColumnCount())){
                    returnValue = getValueAt(row, column).getClass();
                }else{
                    returnValue = Object.class;
                }
                return returnValue;
            }
        };
        model.addColumn("No");
        model.addColumn("Kode Barang");
        model.addColumn("Nama Barang");
        model.addColumn("Merk"); 
        model.addColumn("Satuan"); 
        model.addColumn("Stok"); 
        model.addColumn("Hrg beli satuan"); 
        model.addColumn("Hrg beli pcs"); 
        model.addColumn("Hrg jual satuan"); 
        model.addColumn("Hrg jual pcs");
        
        //menampilkan data database kedalam tabel
        try {
            int no=1;
            String cariitem=tfcari5.getText();
            String sql = "select barang.kode_barang, barang.nama_barang, merk.nama_merk, satuan.nama_satuan, barang.stok, barang.hbsatuan, barang.hbpcs, barang.hjsatuan, barang.hjpcs  from barang JOIN merk ON barang.kode_merk = merk.kode_merk JOIN satuan ON satuan.kode_satuan = barang.kode_satuan where kode_barang like '%"+cariitem+"%' or nama_barang like '%"+cariitem+"%' or merk.nama_merk like '%"+cariitem+"%' or satuan.nama_satuan like '%"+cariitem+"%' or stok like '%"+cariitem+"%' or hbsatuan like '%"+cariitem+"%'or hbpcs like '%"+cariitem+"%' or hjsatuan like '%"+cariitem+"%' or hjpcs like '%"+cariitem+"%' order by kode_barang asc";
            Statement stm=conn.createStatement();
            ResultSet res=stm.executeQuery(sql);
            while(res.next()){
                model.addRow(new Object[]{no++,res.getString(1),res.getString(2),res.getString(3),res.getString(4),res.getString(5),res.getString(6),res.getString(7),res.getString(8),res.getString(9)});
            }
            ResultSet rs=conn.createStatement().executeQuery("select count(*) as jml from barang");
            while(rs.next()){
                jml_brg.setText(String.valueOf(rs.getInt(1)));
            }             
            tBarang.setModel(model);
            RowSorter<TableModel> sorter = new TableRowSorter<>(model);
            tBarang.setRowSorter(sorter);
            //Layouting JTable
            tBarang.getTableHeader().setResizingAllowed(false);
            tBarang.getTableHeader().setReorderingAllowed(false);
            tBarang.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12));
            ((DefaultTableCellRenderer)tBarang.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            ((DefaultTableCellRenderer)tBarang.getTableHeader().getDefaultRenderer()).setVerticalAlignment(SwingConstants.TOP);
            DefaultTableCellRenderer crender = new DefaultTableCellRenderer();
            crender.setHorizontalAlignment(JLabel.CENTER);
            for (int columnIndex=0;columnIndex<tBarang.getColumnCount();columnIndex++){
                tBarang.getColumnModel().getColumn(columnIndex).setCellRenderer(crender);
            }
            tBarang.getColumnModel().getColumn(0).setPreferredWidth(30);
            tBarang.getColumnModel().getColumn(1).setPreferredWidth(120);
            tBarang.getColumnModel().getColumn(2).setPreferredWidth(240);
            tBarang.getColumnModel().getColumn(3).setPreferredWidth(140);
            tBarang.getColumnModel().getColumn(4).setPreferredWidth(100);
            tBarang.getColumnModel().getColumn(5).setPreferredWidth(70);
            tBarang.getColumnModel().getColumn(6).setPreferredWidth(130);
            tBarang.getColumnModel().getColumn(7).setPreferredWidth(110);
            tBarang.getColumnModel().getColumn(8).setPreferredWidth(130);
            tBarang.getColumnModel().getColumn(9).setPreferredWidth(110);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data gagal dipanggil"+e);
        }
    }

   public void dataStok(){
        // membuat tampilan model tabel
        DefaultTableModel model = new DefaultTableModel(){
            public Class getColumnClass(int column, int row){
                Class returnValue;
		if (row!=0){
                    row=0;
		}
                if ((column >=0) && (column < getColumnCount())){
                    returnValue = getValueAt(row, column).getClass();
                }else{
                    returnValue = Object.class;
                }
                return returnValue;
            }
        };
        model.addColumn("No");
        model.addColumn("Kode Barang");
        model.addColumn("Nama Barang");
        model.addColumn("Merk"); 
        model.addColumn("Satuan"); 
        model.addColumn("Stok"); 
        model.addColumn("Hrg beli satuan"); 
        model.addColumn("Hrg beli pcs"); 
        
        //menampilkan data database kedalam tabel
        try {
            int no=1;
            String cariitem = tflimit.getText();
            String sql = "select barang.kode_barang, barang.nama_barang, merk.nama_merk, satuan.nama_satuan, barang.stok, barang.hbsatuan, barang.hbpcs  from barang JOIN merk ON barang.kode_merk = merk.kode_merk JOIN satuan ON satuan.kode_satuan = barang.kode_satuan where stok<='"+cariitem+"' order by kode_barang asc";
            Statement stm=conn.createStatement();
            ResultSet res=stm.executeQuery(sql);
            while(res.next()){
                model.addRow(new Object[]{no++,res.getString(1),res.getString(2),res.getString(3),res.getString(4),res.getString(5),res.getString(6),res.getString(7)});
            }
            ResultSet rs=conn.createStatement().executeQuery("select count(*) as jml from barang");
            while(rs.next()){
                jml_stok.setText(String.valueOf(rs.getInt(1)));
            }             
            tStok.setModel(model);
            RowSorter<TableModel> sorter = new TableRowSorter<>(model);
            tStok.setRowSorter(sorter);
            //Layouting JTable
            tStok.getTableHeader().setResizingAllowed(false);
            tStok.getTableHeader().setReorderingAllowed(false);
            tStok.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12));
            ((DefaultTableCellRenderer)tStok.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            ((DefaultTableCellRenderer)tStok.getTableHeader().getDefaultRenderer()).setVerticalAlignment(SwingConstants.TOP);
            DefaultTableCellRenderer crender = new DefaultTableCellRenderer();
            crender.setHorizontalAlignment(JLabel.CENTER);
            for (int columnIndex=0;columnIndex<tStok.getColumnCount();columnIndex++){
                tStok.getColumnModel().getColumn(columnIndex).setCellRenderer(crender);
            }
            tStok.getColumnModel().getColumn(0).setPreferredWidth(30);
            tStok.getColumnModel().getColumn(1).setPreferredWidth(120);
            tStok.getColumnModel().getColumn(2).setPreferredWidth(240);
            tStok.getColumnModel().getColumn(3).setPreferredWidth(140);
            tStok.getColumnModel().getColumn(4).setPreferredWidth(100);
            tStok.getColumnModel().getColumn(5).setPreferredWidth(70);
            tStok.getColumnModel().getColumn(6).setPreferredWidth(130);
            tStok.getColumnModel().getColumn(7).setPreferredWidth(110);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data gagal dipanggil"+e);
        }
    }

    public static void dataSupplier(){
        // membuat tampilan model tabel
        DefaultTableModel model = new DefaultTableModel(){
            public Class getColumnClass(int column, int row){
                Class returnValue;
		if (row!=0){
                    row=0;
		}
                if ((column >=0) && (column < getColumnCount())){
                    returnValue = getValueAt(row, column).getClass();
                }else{
                    returnValue = Object.class;
                }
                return returnValue;
            }
        };
        model.addColumn("No");
        model.addColumn("Kode Supplier");
        model.addColumn("Nama Supplier");
        model.addColumn("No. Tlp");
        model.addColumn("ALamat");
        
        //menampilkan data database kedalam tabel
        try {
            int no=1;
            String cariitem=tfcari2.getText();
            String sql = "select * from supplier where (kode_supplier!='SUP00') and (nama_supplier like '%"+cariitem+"%' or notelp like '%"+cariitem+"%' or alamat like '%"+cariitem+"%' or kode_supplier like '%"+cariitem+"%') order by kode_supplier asc";
            Statement stm=conn.createStatement();
            ResultSet res=stm.executeQuery(sql);
            while(res.next()){
                model.addRow(new Object[]{no++,res.getString(1),res.getString(2),res.getString(3),res.getString(4)});
            }
            ResultSet rs=conn.createStatement().executeQuery("select count(*) as jml from supplier");
            while(rs.next()){
                jml_supp.setText(String.valueOf(rs.getInt(1)-1));
            }            
            tbl_supp.setModel(model);
            RowSorter<TableModel> sorter = new TableRowSorter<>(model);
            tbl_supp.setRowSorter(sorter);
            
            //Layouting JTable
            tbl_supp.getTableHeader().setResizingAllowed(false);
            tbl_supp.getTableHeader().setReorderingAllowed(false);
            tbl_supp.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12));
            ((DefaultTableCellRenderer)tbl_supp.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            ((DefaultTableCellRenderer)tbl_supp.getTableHeader().getDefaultRenderer()).setVerticalAlignment(SwingConstants.TOP);
            DefaultTableCellRenderer crender = new DefaultTableCellRenderer();
            crender.setHorizontalAlignment(JLabel.CENTER);
            for (int columnIndex=0;columnIndex<tbl_supp.getColumnCount();columnIndex++){
                tbl_supp.getColumnModel().getColumn(columnIndex).setCellRenderer(crender);
            }
            tbl_supp.setRowHeight(25);
            tbl_supp.getColumnModel().getColumn(0).setPreferredWidth(30);
            tbl_supp.getColumnModel().getColumn(1).setPreferredWidth(200);
            tbl_supp.getColumnModel().getColumn(2).setPreferredWidth(200);
            tbl_supp.getColumnModel().getColumn(3).setPreferredWidth(150);
            tbl_supp.getColumnModel().getColumn(4).setPreferredWidth(150);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data gagal dipanggil"+e);
        }
    }    
    
    public static void dataPelanggan(){
        // membuat tampilan model tabel
        DefaultTableModel model = new DefaultTableModel(){
            public Class getColumnClass(int column, int row){
                Class returnValue;
		if (row!=0){
                    row=0;
		}
                if ((column >=0) && (column < getColumnCount())){
                    returnValue = getValueAt(row, column).getClass();
                }else{
                    returnValue = Object.class;
                }
                return returnValue;
            }
        };
        model.addColumn("No");
        model.addColumn("Kode Pelanggan");
        model.addColumn("Nama Pelanggan");
        model.addColumn("No. Tlp");
        model.addColumn("ALamat");        
        //menampilkan data database kedalam tabel
        try {
            int no=1;
            String cariitem=tfcari1.getText();
            String sql = "select * from pelanggan where (kode_pelanggan!='PLG00') and (nama_pelanggan like '%"+cariitem+"%' or notelp like '%"+cariitem+"%' or alamat like '%"+cariitem+"%' or kode_pelanggan like '%"+cariitem+"%') order by kode_pelanggan asc";
            Statement stm=conn.createStatement();
            ResultSet res=stm.executeQuery(sql);
            while(res.next()){
                model.addRow(new Object[]{no++,res.getString(1),res.getString(2),res.getString(3),res.getString(4)});
            }
            ResultSet rs=conn.createStatement().executeQuery("select count(*) as jml from pelanggan");
            while(rs.next()){
                jml_plg.setText(String.valueOf(rs.getInt(1)-1));
            }            
            tbl_plg.setModel(model);
            RowSorter<TableModel> sorter = new TableRowSorter<>(model);
            tbl_plg.setRowSorter(sorter);            
            //Layouting JTable
            tbl_plg.getTableHeader().setResizingAllowed(false);
            tbl_plg.getTableHeader().setReorderingAllowed(false);
            tbl_plg.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12));
            ((DefaultTableCellRenderer)tbl_plg.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            ((DefaultTableCellRenderer)tbl_plg.getTableHeader().getDefaultRenderer()).setVerticalAlignment(SwingConstants.TOP);
            DefaultTableCellRenderer crender = new DefaultTableCellRenderer();
            crender.setHorizontalAlignment(JLabel.CENTER);
            for (int columnIndex=0;columnIndex<tbl_plg.getColumnCount();columnIndex++){
                tbl_plg.getColumnModel().getColumn(columnIndex).setCellRenderer(crender);
            }
            tbl_plg.setRowHeight(25);
            tbl_plg.getColumnModel().getColumn(0).setPreferredWidth(30);
            tbl_plg.getColumnModel().getColumn(1).setPreferredWidth(200);
            tbl_plg.getColumnModel().getColumn(2).setPreferredWidth(200);
            tbl_plg.getColumnModel().getColumn(3).setPreferredWidth(150);
            tbl_plg.getColumnModel().getColumn(4).setPreferredWidth(150);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data gagal dipanggil"+e);
        }
    }

    public static void dataMerk(){
        // membuat tampilan model tabel
        DefaultTableModel model = new DefaultTableModel(){
            public Class getColumnClass(int column, int row){
                Class returnValue;
		if (row!=0){
                    row=0;
		}
                if ((column >=0) && (column < getColumnCount())){
                    returnValue = getValueAt(row, column).getClass();
                }else{
                    returnValue = Object.class;
                }
                return returnValue;
            }
        };
        model.addColumn("No");
        model.addColumn("Kode Merk");
        model.addColumn("Nama Merk");
        
        //menampilkan data database kedalam tabel
        try {
            int no=1;
            String cariitem=tfcari4.getText();
            String sql = "select * from merk where kode_merk like '%"+cariitem+"%' or nama_merk like '%"+cariitem+"%' order by kode_merk asc";
            Statement stm=conn.createStatement();
            ResultSet res=stm.executeQuery(sql);
            while(res.next()){
                model.addRow(new Object[]{no++,res.getString(1),res.getString(2)});
            }
            ResultSet rs=conn.createStatement().executeQuery("select count(*) as jml from merk");
            while(rs.next()){
                jml_merk.setText(String.valueOf(rs.getInt(1)));
            }            
            tbl_merk.setModel(model);
            RowSorter<TableModel> sorter = new TableRowSorter<>(model);
            tbl_merk.setRowSorter(sorter);
            
            //Layouting JTable
            tbl_merk.getTableHeader().setResizingAllowed(false);
            tbl_merk.getTableHeader().setReorderingAllowed(false);
            tbl_merk.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12));
            ((DefaultTableCellRenderer)tbl_merk.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            ((DefaultTableCellRenderer)tbl_merk.getTableHeader().getDefaultRenderer()).setVerticalAlignment(SwingConstants.TOP);
            DefaultTableCellRenderer crender = new DefaultTableCellRenderer();
            crender.setHorizontalAlignment(JLabel.CENTER);
            for (int columnIndex=0;columnIndex<tbl_merk.getColumnCount();columnIndex++){
                tbl_merk.getColumnModel().getColumn(columnIndex).setCellRenderer(crender);
            }
            tbl_merk.setRowHeight(25);
            tbl_merk.getColumnModel().getColumn(0).setPreferredWidth(35);
            tbl_merk.getColumnModel().getColumn(1).setPreferredWidth(200);
            tbl_merk.getColumnModel().getColumn(2).setPreferredWidth(350);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data gagal dipanggil"+e);
        }
    }
    
    public static void dataSatuan(){
        // membuat tampilan model tabel
        DefaultTableModel model = new DefaultTableModel(){
            public Class getColumnClass(int column, int row){
                Class returnValue;
		if (row!=0){
                    row=0;
		}
                if ((column >=0) && (column < getColumnCount())){
                    returnValue = getValueAt(row, column).getClass();
                }else{
                    returnValue = Object.class;
                }
                return returnValue;
            }
        };
        model.addColumn("No");
        model.addColumn("Kode Satuan");
        model.addColumn("Nama Satuan");
        model.addColumn("Isi");
        
        //menampilkan data database kedalam tabel
        try {
            int no=1;
            String cariitem=tfcari3.getText();
            String sql = "select * from satuan where kode_satuan like '%"+cariitem+"%' or nama_satuan like '%"+cariitem+"%' or isi like '%"+cariitem+"%' order by kode_satuan asc";
            Statement stm=conn.createStatement();
            ResultSet res=stm.executeQuery(sql);
            while(res.next()){
                model.addRow(new Object[]{no++,res.getString(1),res.getString(2),res.getString(3)});
            }
            ResultSet rs=conn.createStatement().executeQuery("select count(*) as jml from satuan");
            while(rs.next()){
                jml_stn.setText(String.valueOf(rs.getInt(1)));
            }
            tbl_stn.setModel(model);
            RowSorter<TableModel> sorter = new TableRowSorter<>(model);
            tbl_stn.setRowSorter(sorter);
            
            //Layouting JTable
            tbl_stn.getTableHeader().setResizingAllowed(false);
            tbl_stn.getTableHeader().setReorderingAllowed(false);
            tbl_stn.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12));
            ((DefaultTableCellRenderer)tbl_stn.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            ((DefaultTableCellRenderer)tbl_stn.getTableHeader().getDefaultRenderer()).setVerticalAlignment(SwingConstants.TOP);
            DefaultTableCellRenderer crender = new DefaultTableCellRenderer();
            crender.setHorizontalAlignment(JLabel.CENTER);
            for (int columnIndex=0;columnIndex<tbl_stn.getColumnCount();columnIndex++){
                tbl_stn.getColumnModel().getColumn(columnIndex).setCellRenderer(crender);
            }
            tbl_stn.setRowHeight(25);
            tbl_stn.getColumnModel().getColumn(0).setPreferredWidth(30);
            tbl_stn.getColumnModel().getColumn(1).setPreferredWidth(150);
            tbl_stn.getColumnModel().getColumn(2).setPreferredWidth(200);
            tbl_stn.getColumnModel().getColumn(3).setPreferredWidth(170);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data gagal dipanggil"+e);
        }
    }
    
    public static void dataPegawai(){
        // membuat tampilan model tabel
        DefaultTableModel model = new DefaultTableModel(){
            public Class getColumnClass(int column, int row){
                Class returnValue;
		if (row!=0){
                    row=0;
		}
                if ((column >=0) && (column < getColumnCount())){
                    returnValue = getValueAt(row, column).getClass();
                }else{
                    returnValue = Object.class;
                }
                return returnValue;
            }
        };
        model.addColumn("No");
        model.addColumn("ID");
        model.addColumn("Nama Pegawai");
        model.addColumn("Tanggal Lahir"); 
        model.addColumn("Jenis Kelamin"); 
        model.addColumn("Alamat"); 
        model.addColumn("No. Tlp"); 
        model.addColumn("Jabatan");        
        //menampilkan data database kedalam tabel
        try {
            int no=1;
            String pgwsearch=tfcari.getText();
            String sql = "select * from pegawai where kode_pegawai like '%"+pgwsearch+"%' or nama_pegawai like '%"+pgwsearch+"%' or jkel like '%"+pgwsearch+"%' or alamat like '%"+pgwsearch+"%' or notlp like '%"+pgwsearch+"%' order by kode_pegawai asc";
            String sqlsum = "select count(*) as jml from pegawai";
            Statement stm=conn.createStatement();
            Statement st=conn.createStatement();
            ResultSet res=stm.executeQuery(sql);
            ResultSet rs=st.executeQuery(sqlsum);
            while(res.next()){
                String tglp = res.getString(3);
                String tglpg="";
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(tglp);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    tglpg = sdf.format(date);
                } catch (ParseException ex) {
                    Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                }
                model.addRow(new Object[]{no++,res.getString(1),res.getString(2),tglpg,res.getString(4),res.getString(5),res.getString(6),res.getString(7)});
            }
            while(rs.next()){
                jml_pgw.setText(String.valueOf(rs.getInt(1)));
            }            
            tbl_pgw.setModel(model);
            RowSorter<TableModel> sorter = new TableRowSorter<>(model);
            tbl_pgw.setRowSorter(sorter);
            //Layouting JTable
            tbl_pgw.getTableHeader().setResizingAllowed(false);
            tbl_pgw.getTableHeader().setReorderingAllowed(false);
            tbl_pgw.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,13));
            ((DefaultTableCellRenderer)tbl_pgw.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            ((DefaultTableCellRenderer)tbl_pgw.getTableHeader().getDefaultRenderer()).setVerticalAlignment(SwingConstants.TOP);
            DefaultTableCellRenderer crender = new DefaultTableCellRenderer();
            crender.setHorizontalAlignment(JLabel.CENTER);
            for (int columnIndex=0;columnIndex<tbl_pgw.getColumnCount();columnIndex++){
                tbl_pgw.getColumnModel().getColumn(columnIndex).setCellRenderer(crender);
            }
            tbl_pgw.setRowHeight(25);
            tbl_pgw.getColumnModel().getColumn(0).setPreferredWidth(30);
            tbl_pgw.getColumnModel().getColumn(1).setPreferredWidth(100);
            tbl_pgw.getColumnModel().getColumn(2).setPreferredWidth(190);
            tbl_pgw.getColumnModel().getColumn(3).setPreferredWidth(110);
            tbl_pgw.getColumnModel().getColumn(4).setPreferredWidth(110);
            tbl_pgw.getColumnModel().getColumn(5).setPreferredWidth(100);
            tbl_pgw.getColumnModel().getColumn(6).setPreferredWidth(100);
            tbl_pgw.getColumnModel().getColumn(7).setPreferredWidth(100);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data gagal dipanggil"+e);
        }
    }
    
    private void dataUsers(){
        // membuat tampilan model tabel
        DefaultTableModel model = new DefaultTableModel(){
            public Class getColumnClass(int column, int row){
                Class returnValue;
		if (row!=0){
                    row=0;
		}
                if ((column >=0) && (column < getColumnCount())){
                    returnValue = getValueAt(row, column).getClass();
                }else{
                    returnValue = Object.class;
                }
                return returnValue;
            }
        };
        model.addColumn("No");
        model.addColumn("Kode User");
        model.addColumn("Nama");
        model.addColumn("Username"); 
        model.addColumn("Password"); 
        model.addColumn("Level");
        
        //menampilkan data database kedalam tabel
        try {
            int no=1;
            String sql = "select * from users JOIN pegawai ON users.kode_pegawai=pegawai.kode_pegawai";
            String sqlsum = "select count(*) as jml from users";
            Statement stm=conn.createStatement();
            ResultSet res=stm.executeQuery(sql);
            Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery(sqlsum);
            while(res.next()){
                model.addRow(new Object[]{no++,res.getString("kode_pegawai"),res.getString("nama_pegawai"),res.getString("username"),res.getString("password"),res.getString("jabatan")});
            }
            while(rs.next()){
                jmlUsr.setText(String.valueOf(rs.getInt(1)));
            }             
            tUsers.setModel(model);
            RowSorter<TableModel> sorter = new TableRowSorter<>(model);
            tUsers.setRowSorter(sorter);
            //Layouting JTable
            tUsers.getTableHeader().setResizingAllowed(false);
            tUsers.getTableHeader().setReorderingAllowed(false);
            tUsers.getTableHeader().setFont(new Font("Tahoma",Font.BOLD,12));
            ((DefaultTableCellRenderer)tUsers.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            ((DefaultTableCellRenderer)tUsers.getTableHeader().getDefaultRenderer()).setVerticalAlignment(SwingConstants.TOP);
            DefaultTableCellRenderer crender = new DefaultTableCellRenderer();
            crender.setHorizontalAlignment(JLabel.CENTER);
            for (int columnIndex=0;columnIndex<tUsers.getColumnCount();columnIndex++){
                tUsers.getColumnModel().getColumn(columnIndex).setCellRenderer(crender);
            }
            tUsers.getColumnModel().getColumn(0).setPreferredWidth(25);
            tUsers.getColumnModel().getColumn(1).setPreferredWidth(90);
            tUsers.getColumnModel().getColumn(2).setPreferredWidth(180);
            tUsers.getColumnModel().getColumn(3).setPreferredWidth(110);
            tUsers.getColumnModel().getColumn(4).setPreferredWidth(110);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data gagal dipanggil"+e);
        }
    }

    public  void getKdUsr(){
        try {
            cbKdUsr.removeAllItems();
            ResultSet rs = conn.createStatement().executeQuery("select * from pegawai");
            while(rs.next()){
                cbKdUsr.addItem(rs.getString("kode_pegawai"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error " + e);
        }
    }
    
    public void kosongUser(){
        cbKdUsr.setSelectedItem(null);
        txtkode_temp.setText("");
        tfnama.setText("");
        tfuname.setText("");
        tfpass.setText("");
        tfLevel.setText("");
        uImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/avatar.png")));
    }

    String imgPath = null;
    public ImageIcon ResizeImage(String ImagePath, byte[] pic, JLabel label)
    {
        ImageIcon MyImage = null;
        if(ImagePath != null)
        {
           MyImage = new ImageIcon(ImagePath);
        }else
        {
            MyImage = new ImageIcon(pic);
        }
        Image img = MyImage.getImage();
        Image newImg = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);
        return image;
    }
    
    private void getDateTime(){
        //date
        String months[] = {
            "Januari", "Februari", "Maret", "April",
            "Mei", "Juni", "Juli", "Agustus",
            "September", "Oktober", "November", "Desember"
        };
        GregorianCalendar gcalendar = new GregorianCalendar();
        String tgl = String.valueOf(gcalendar.get(Calendar.DATE));
        String bln = String.valueOf(months[gcalendar.get(Calendar.MONTH)]);
        String thn = String.valueOf(gcalendar.get(Calendar.YEAR));
        tfadate.setText(tgl+" "+bln+" "+thn);
        
        //time
        Timer timer = new Timer(1000, (ActionEvent e) -> {
            DateTimeFormatter myTime = DateTimeFormatter.ofPattern("hh : mm a");
            LocalDateTime now = LocalDateTime.now();
            String timenow =String.valueOf(myTime.format(now));
            tfatime.setText(timenow);
        });
        timer.setRepeats(true);
        timer.start();
    }
    
    public void getSummary(){
        try {
            ResultSet rs=conn.createStatement().executeQuery("select count(*) from barang");
            ResultSet rs1=conn.createStatement().executeQuery("select count(*) from pelanggan");
            ResultSet rs2=conn.createStatement().executeQuery("select count(*) from supplier");
            ResultSet rs3=conn.createStatement().executeQuery("select count(*) from merk");
            ResultSet rs4=conn.createStatement().executeQuery("select count(*) from satuan");
            ResultSet rs5=conn.createStatement().executeQuery("select count(*) from pegawai");
            while(rs.next()&&rs1.next()&&rs2.next()&&rs3.next()&&rs4.next()&&rs5.next()){
                jb.setText(String.valueOf(rs.getInt(1)));
                jpel.setText(String.valueOf(rs1.getInt(1)));
                jsup.setText(String.valueOf(rs2.getInt(1)));
                jm.setText(String.valueOf(rs3.getInt(1)));
                jsat.setText(String.valueOf(rs4.getInt(1)));
                jpeg.setText(String.valueOf(rs5.getInt(1)));
                
            }              
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new javax.swing.JPanel();
        bgmain = new javax.swing.JPanel();
        main = new javax.swing.JPanel();
        min = new javax.swing.JLabel();
        exit = new javax.swing.JLabel();
        sidepane = new javax.swing.JPanel();
        imgUser = new javax.swing.JLabel();
        lbl_name = new javax.swing.JLabel();
        lbl_level = new javax.swing.JLabel();
        bDash = new javax.swing.JPanel();
        act1 = new javax.swing.JPanel();
        ldash = new javax.swing.JLabel();
        bMaster = new javax.swing.JPanel();
        act2 = new javax.swing.JPanel();
        lmaster = new javax.swing.JLabel();
        bTrans = new javax.swing.JPanel();
        act3 = new javax.swing.JPanel();
        ltrans = new javax.swing.JLabel();
        bLap = new javax.swing.JPanel();
        act4 = new javax.swing.JPanel();
        llap = new javax.swing.JLabel();
        bMUser = new javax.swing.JPanel();
        act5 = new javax.swing.JPanel();
        lmuser = new javax.swing.JLabel();
        bKeluar = new javax.swing.JPanel();
        act6 = new javax.swing.JPanel();
        lkeluar = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        mainpane = new javax.swing.JPanel();
        dashAdmin = new javax.swing.JPanel();
        jPanel75 = new javax.swing.JPanel();
        jLabel75 = new javax.swing.JLabel();
        admName = new javax.swing.JLabel();
        jPanel89 = new javax.swing.JPanel();
        jPanel90 = new javax.swing.JPanel();
        tfatime = new javax.swing.JLabel();
        jPanel91 = new javax.swing.JPanel();
        tfadate = new javax.swing.JLabel();
        jPanel78 = new javax.swing.JPanel();
        jPanel94 = new javax.swing.JPanel();
        jPanel98 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jb = new javax.swing.JLabel();
        jPanel95 = new javax.swing.JPanel();
        jPanel99 = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jpel = new javax.swing.JLabel();
        jPanel96 = new javax.swing.JPanel();
        jPanel100 = new javax.swing.JPanel();
        jLabel80 = new javax.swing.JLabel();
        jsup = new javax.swing.JLabel();
        jPanel97 = new javax.swing.JPanel();
        jPanel101 = new javax.swing.JPanel();
        jLabel83 = new javax.swing.JLabel();
        jm = new javax.swing.JLabel();
        jPanel87 = new javax.swing.JPanel();
        jPanel102 = new javax.swing.JPanel();
        jLabel86 = new javax.swing.JLabel();
        jsat = new javax.swing.JLabel();
        jPanel88 = new javax.swing.JPanel();
        jPanel103 = new javax.swing.JPanel();
        jLabel87 = new javax.swing.JLabel();
        jpeg = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        dashGudang = new javax.swing.JPanel();
        jPanel68 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        ptgsName = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tStok = new javax.swing.JTable();
        jPanel74 = new javax.swing.JPanel();
        jLabel77 = new javax.swing.JLabel();
        jml_stok = new javax.swing.JLabel();
        jPanel76 = new javax.swing.JPanel();
        jPanel69 = new javax.swing.JPanel();
        jLabel74 = new javax.swing.JLabel();
        tflimit = new javax.swing.JTextField();
        btnCetak = new javax.swing.JPanel();
        bCetak = new javax.swing.JLabel();
        dashKasir = new javax.swing.JPanel();
        jPanel92 = new javax.swing.JPanel();
        jLabel98 = new javax.swing.JLabel();
        ptgsName1 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        master = new javax.swing.JPanel();
        bg1 = new javax.swing.JPanel();
        barang = new javax.swing.JPanel();
        iconBarang = new javax.swing.JLabel();
        lb_barang = new javax.swing.JLabel();
        bg3 = new javax.swing.JPanel();
        pegawai = new javax.swing.JPanel();
        iconPegawai = new javax.swing.JLabel();
        lb_pegawai = new javax.swing.JLabel();
        bg4 = new javax.swing.JPanel();
        merk = new javax.swing.JPanel();
        iconMerk = new javax.swing.JLabel();
        lb_merk = new javax.swing.JLabel();
        bg5 = new javax.swing.JPanel();
        satuan = new javax.swing.JPanel();
        iconSatuan = new javax.swing.JLabel();
        lb_satuan = new javax.swing.JLabel();
        bg6 = new javax.swing.JPanel();
        supplier = new javax.swing.JPanel();
        iconSupplier = new javax.swing.JLabel();
        lb_supplier = new javax.swing.JLabel();
        bg2 = new javax.swing.JPanel();
        pelanggan = new javax.swing.JPanel();
        iconPelanggan = new javax.swing.JLabel();
        lb_pelanggan = new javax.swing.JLabel();
        master_barang = new javax.swing.JPanel();
        jPanel36 = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jPanel37 = new DropShadowPanel(5, 218, 218, 218);
        jPanel38 = new javax.swing.JPanel();
        btnTambah5 = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        btnUbah5 = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        btnHapus5 = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        panelSearch5 = new javax.swing.JPanel();
        tfcari5 = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        jPanel39 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tBarang = new javax.swing.JTable();
        jPanel40 = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jml_brg = new javax.swing.JLabel();
        sum_cari5 = new javax.swing.JLabel();
        master_pelanggan = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel17 = new DropShadowPanel(5, 218, 218, 218);
        jPanel18 = new javax.swing.JPanel();
        btnTambah1 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        btnUbah1 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        btnHapus1 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        panelSearch1 = new javax.swing.JPanel();
        tfcari1 = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        jLabel55 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_plg = new javax.swing.JTable();
        jPanel22 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jml_plg = new javax.swing.JLabel();
        sum_cari1 = new javax.swing.JLabel();
        master_supplier = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jPanel20 = new DropShadowPanel(5, 218, 218, 218);
        jPanel23 = new javax.swing.JPanel();
        btnTambah2 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        btnUbah2 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        btnHapus2 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        panelSearch2 = new javax.swing.JPanel();
        tfcari2 = new javax.swing.JTextField();
        jPanel41 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jPanel24 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbl_supp = new javax.swing.JTable();
        jPanel25 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jml_supp = new javax.swing.JLabel();
        sum_cari2 = new javax.swing.JLabel();
        master_merk = new javax.swing.JPanel();
        jPanel31 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jPanel32 = new DropShadowPanel(5, 218, 218, 218);
        jPanel33 = new javax.swing.JPanel();
        btnTambah4 = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        btnUbah4 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        btnHapus4 = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        panelSearch4 = new javax.swing.JPanel();
        tfcari4 = new javax.swing.JTextField();
        jPanel42 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jPanel34 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tbl_merk = new javax.swing.JTable();
        jPanel35 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jml_merk = new javax.swing.JLabel();
        sum_cari4 = new javax.swing.JLabel();
        master_satuan = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jPanel27 = new DropShadowPanel(5, 218, 218, 218);
        jPanel28 = new javax.swing.JPanel();
        btnTambah3 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        btnUbah3 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        btnHapus3 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        panelSearch3 = new javax.swing.JPanel();
        tfcari3 = new javax.swing.JTextField();
        jPanel43 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        jPanel29 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbl_stn = new javax.swing.JTable();
        jPanel30 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jml_stn = new javax.swing.JLabel();
        sum_cari3 = new javax.swing.JLabel();
        master_pegawai = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel2 = new DropShadowPanel(5, 218, 218, 218);
        jPanel3 = new javax.swing.JPanel();
        btnTambah = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnUbah = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        btnHapus = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        panelSearch = new javax.swing.JPanel();
        tfcari = new javax.swing.JTextField();
        jPanel44 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_pgw = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jml_pgw = new javax.swing.JLabel();
        sum_cari = new javax.swing.JLabel();
        trans = new javax.swing.JPanel();
        bgjual = new javax.swing.JPanel();
        jual = new javax.swing.JPanel();
        iconSell = new javax.swing.JLabel();
        lb_sell = new javax.swing.JLabel();
        bgbeli = new javax.swing.JPanel();
        beli = new javax.swing.JPanel();
        iconBuy = new javax.swing.JLabel();
        lb_buy = new javax.swing.JLabel();
        lap = new javax.swing.JPanel();
        jPanel4 = new DropShadowPanel(5, 218, 218, 218);
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        tgl4 = new com.toedter.calendar.JDateChooser();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        cbBulan = new javax.swing.JComboBox<>();
        tahun = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jPanel45 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        tftahun = new javax.swing.JTextField();
        jPanel46 = new DropShadowPanel(5, 218, 218, 218);
        jPanel47 = new javax.swing.JPanel();
        jPanel48 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jPanel49 = new javax.swing.JPanel();
        jPanel50 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        tgl3 = new com.toedter.calendar.JDateChooser();
        jPanel51 = new javax.swing.JPanel();
        jPanel52 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        cbBulan1 = new javax.swing.JComboBox<>();
        tahun1 = new javax.swing.JLabel();
        jPanel53 = new javax.swing.JPanel();
        jPanel54 = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        tftahun1 = new javax.swing.JTextField();
        jPanel55 = new DropShadowPanel(5, 218, 218, 218);
        jPanel56 = new javax.swing.JPanel();
        jPanel57 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jPanel58 = new DropShadowPanel(5, 218, 218, 218);
        jPanel59 = new javax.swing.JPanel();
        jPanel60 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jPanel61 = new javax.swing.JPanel();
        jPanel62 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        xtgl1 = new com.toedter.calendar.JDateChooser();
        xtgl2 = new com.toedter.calendar.JDateChooser();
        mngUser = new javax.swing.JPanel();
        jPanel63 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jPanel64 = new javax.swing.JPanel();
        jPanel65 = new javax.swing.JPanel();
        btnTambah6 = new javax.swing.JPanel();
        jLabel59 = new javax.swing.JLabel();
        jPanel67 = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        jmlUsr = new javax.swing.JLabel();
        btnUbah6 = new javax.swing.JPanel();
        jLabel60 = new javax.swing.JLabel();
        btnHapus6 = new javax.swing.JPanel();
        jLabel61 = new javax.swing.JLabel();
        jPanel70 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tUsers = new javax.swing.JTable();
        jPanel71 = new javax.swing.JPanel();
        jPanel72 = new javax.swing.JPanel();
        lbAksi = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        cbKdUsr = new javax.swing.JComboBox<>();
        jPanel73 = new javax.swing.JPanel();
        ulnama = new javax.swing.JPanel();
        jLabel66 = new javax.swing.JLabel();
        tfnama = new javax.swing.JTextField();
        uluname = new javax.swing.JPanel();
        jLabel67 = new javax.swing.JLabel();
        tfuname = new javax.swing.JTextField();
        ulpass = new javax.swing.JPanel();
        jLabel68 = new javax.swing.JLabel();
        tfpass = new javax.swing.JTextField();
        jPanel77 = new javax.swing.JPanel();
        jLabel69 = new javax.swing.JLabel();
        btnSimpan6 = new javax.swing.JPanel();
        jLabel73 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        uImg = new javax.swing.JLabel();
        btnBatal = new javax.swing.JPanel();
        jLabel72 = new javax.swing.JLabel();
        txtkode_temp = new javax.swing.JTextField();
        iuname = new javax.swing.JLabel();
        ipass = new javax.swing.JLabel();
        tfLevel = new javax.swing.JTextField();
        jPanel66 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        bg.setBackground(new java.awt.Color(0, 0, 0, 100));

        bgmain.setBackground(new java.awt.Color(0, 0, 0, 0));
        bgmain.setLayout(new java.awt.GridBagLayout());

        main.setBackground(new java.awt.Color(255, 255, 255));
        main.setPreferredSize(new java.awt.Dimension(1300, 698));
        main.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        min.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        min.setForeground(new java.awt.Color(25, 181, 254));
        min.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        min.setText("-");
        min.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        min.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                minMouseClicked(evt);
            }
        });
        main.add(min, new org.netbeans.lib.awtextra.AbsoluteConstraints(1250, 0, 20, 25));

        exit.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        exit.setForeground(new java.awt.Color(25, 181, 254));
        exit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        exit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/closeb.png"))); // NOI18N
        exit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitMouseClicked(evt);
            }
        });
        main.add(exit, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 0, 30, 25));

        sidepane.setBackground(new java.awt.Color(255, 255, 255));
        sidepane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        imgUser.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imgUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/avatar.png"))); // NOI18N
        imgUser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));
        sidepane.add(imgUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, 120, 120));

        lbl_name.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lbl_name.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_name.setText("Nama User");
        sidepane.add(lbl_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 220, -1));

        lbl_level.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lbl_level.setForeground(new java.awt.Color(153, 153, 153));
        lbl_level.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_level.setText("Level");
        lbl_level.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        sidepane.add(lbl_level, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 220, 20));

        bDash.setBackground(new java.awt.Color(255, 255, 255));
        bDash.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bDash.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bDashMousePressed(evt);
            }
        });
        bDash.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        act1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout act1Layout = new javax.swing.GroupLayout(act1);
        act1.setLayout(act1Layout);
        act1Layout.setHorizontalGroup(
            act1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        act1Layout.setVerticalGroup(
            act1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        bDash.add(act1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 5, 30));

        ldash.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        ldash.setForeground(new java.awt.Color(153, 153, 153));
        ldash.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/home_g.png"))); // NOI18N
        ldash.setText("Dashboard");
        ldash.setIconTextGap(20);
        bDash.add(ldash, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 210, 50));

        sidepane.add(bDash, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 250, 240, 50));

        bMaster.setBackground(new java.awt.Color(255, 255, 255));
        bMaster.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bMaster.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bMasterMousePressed(evt);
            }
        });
        bMaster.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        act2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout act2Layout = new javax.swing.GroupLayout(act2);
        act2.setLayout(act2Layout);
        act2Layout.setHorizontalGroup(
            act2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        act2Layout.setVerticalGroup(
            act2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        bMaster.add(act2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 5, 30));

        lmaster.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lmaster.setForeground(new java.awt.Color(153, 153, 153));
        lmaster.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/master_g.png"))); // NOI18N
        lmaster.setText("Master");
        lmaster.setIconTextGap(20);
        bMaster.add(lmaster, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 210, 50));

        sidepane.add(bMaster, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 300, 240, 50));

        bTrans.setBackground(new java.awt.Color(255, 255, 255));
        bTrans.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bTrans.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bTransMousePressed(evt);
            }
        });
        bTrans.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        act3.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout act3Layout = new javax.swing.GroupLayout(act3);
        act3.setLayout(act3Layout);
        act3Layout.setHorizontalGroup(
            act3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        act3Layout.setVerticalGroup(
            act3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        bTrans.add(act3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 5, 30));

        ltrans.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        ltrans.setForeground(new java.awt.Color(153, 153, 153));
        ltrans.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/trans_g.png"))); // NOI18N
        ltrans.setText("Transaksi");
        ltrans.setIconTextGap(20);
        bTrans.add(ltrans, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 210, 50));

        sidepane.add(bTrans, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 350, 240, 50));

        bLap.setBackground(new java.awt.Color(255, 255, 255));
        bLap.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bLap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bLapMousePressed(evt);
            }
        });
        bLap.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        act4.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout act4Layout = new javax.swing.GroupLayout(act4);
        act4.setLayout(act4Layout);
        act4Layout.setHorizontalGroup(
            act4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        act4Layout.setVerticalGroup(
            act4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        bLap.add(act4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 5, 30));

        llap.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        llap.setForeground(new java.awt.Color(153, 153, 153));
        llap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/report_g.png"))); // NOI18N
        llap.setText("Laporan");
        llap.setIconTextGap(20);
        bLap.add(llap, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 210, 50));

        sidepane.add(bLap, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 400, 240, 50));

        bMUser.setBackground(new java.awt.Color(255, 255, 255));
        bMUser.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bMUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bMUserMousePressed(evt);
            }
        });
        bMUser.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        act5.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout act5Layout = new javax.swing.GroupLayout(act5);
        act5.setLayout(act5Layout);
        act5Layout.setHorizontalGroup(
            act5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        act5Layout.setVerticalGroup(
            act5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        bMUser.add(act5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 5, 30));

        lmuser.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lmuser.setForeground(new java.awt.Color(153, 153, 153));
        lmuser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/mnguser_g.png"))); // NOI18N
        lmuser.setText("Manage User");
        lmuser.setIconTextGap(20);
        bMUser.add(lmuser, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 210, 50));

        sidepane.add(bMUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 450, 240, 50));

        bKeluar.setBackground(new java.awt.Color(255, 255, 255));
        bKeluar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bKeluar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bKeluarMousePressed(evt);
            }
        });
        bKeluar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        act6.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout act6Layout = new javax.swing.GroupLayout(act6);
        act6.setLayout(act6Layout);
        act6Layout.setHorizontalGroup(
            act6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        act6Layout.setVerticalGroup(
            act6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        bKeluar.add(act6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 5, 30));

        lkeluar.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lkeluar.setForeground(new java.awt.Color(153, 153, 153));
        lkeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/exit_g.png"))); // NOI18N
        lkeluar.setText("Keluar");
        lkeluar.setIconTextGap(20);
        bKeluar.add(lkeluar, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 210, 50));

        sidepane.add(bKeluar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 500, 240, 50));

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(204, 204, 204));
        jLabel5.setText("ATK Store Manager");
        sidepane.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, 190, 25));

        jLabel62.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(25, 181, 254));
        jLabel62.setText("ASM");
        sidepane.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 25));

        jLabel64.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel64.setForeground(new java.awt.Color(204, 204, 204));
        jLabel64.setText("-");
        sidepane.add(jLabel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, -1, 25));

        main.add(sidepane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 698));

        mainpane.setLayout(new java.awt.CardLayout());

        dashAdmin.setBackground(new java.awt.Color(248, 248, 248));

        jPanel75.setBackground(new java.awt.Color(235, 248, 252));

        jLabel75.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel75.setForeground(new java.awt.Color(25, 181, 254));
        jLabel75.setText("Selamat Datang,");

        admName.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        admName.setForeground(new java.awt.Color(25, 181, 254));
        admName.setText("$Name !");

        javax.swing.GroupLayout jPanel75Layout = new javax.swing.GroupLayout(jPanel75);
        jPanel75.setLayout(jPanel75Layout);
        jPanel75Layout.setHorizontalGroup(
            jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel75Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel75)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(admName, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel75Layout.setVerticalGroup(
            jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel75Layout.createSequentialGroup()
                .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(admName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel89.setBackground(new java.awt.Color(248, 248, 248));
        jPanel89.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));
        jPanel89.setPreferredSize(new java.awt.Dimension(82, 50));

        tfatime.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        tfatime.setForeground(new java.awt.Color(25, 181, 254));
        tfatime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tfatime.setText("Time");

        javax.swing.GroupLayout jPanel90Layout = new javax.swing.GroupLayout(jPanel90);
        jPanel90.setLayout(jPanel90Layout);
        jPanel90Layout.setHorizontalGroup(
            jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel90Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tfatime, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel90Layout.setVerticalGroup(
            jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tfatime, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );

        tfadate.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        tfadate.setForeground(new java.awt.Color(25, 181, 254));
        tfadate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tfadate.setText("Date");

        javax.swing.GroupLayout jPanel91Layout = new javax.swing.GroupLayout(jPanel91);
        jPanel91.setLayout(jPanel91Layout);
        jPanel91Layout.setHorizontalGroup(
            jPanel91Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel91Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tfadate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel91Layout.setVerticalGroup(
            jPanel91Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tfadate, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel89Layout = new javax.swing.GroupLayout(jPanel89);
        jPanel89.setLayout(jPanel89Layout);
        jPanel89Layout.setHorizontalGroup(
            jPanel89Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel90, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel91, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel89Layout.setVerticalGroup(
            jPanel89Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel89Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel90, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel91, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54))
        );

        jPanel78.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel78Layout = new javax.swing.GroupLayout(jPanel78);
        jPanel78.setLayout(jPanel78Layout);
        jPanel78Layout.setHorizontalGroup(
            jPanel78Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel78Layout.setVerticalGroup(
            jPanel78Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel94.setBackground(new java.awt.Color(248, 248, 248));
        jPanel94.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        jLabel12.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(188, 188, 188));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Jumlah Barang");

        javax.swing.GroupLayout jPanel98Layout = new javax.swing.GroupLayout(jPanel98);
        jPanel98.setLayout(jPanel98Layout);
        jPanel98Layout.setHorizontalGroup(
            jPanel98Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
        );
        jPanel98Layout.setVerticalGroup(
            jPanel98Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        jb.setFont(new java.awt.Font("SansSerif", 1, 48)); // NOI18N
        jb.setForeground(new java.awt.Color(25, 181, 254));
        jb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jb.setText("x");

        javax.swing.GroupLayout jPanel94Layout = new javax.swing.GroupLayout(jPanel94);
        jPanel94.setLayout(jPanel94Layout);
        jPanel94Layout.setHorizontalGroup(
            jPanel94Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel98, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel94Layout.setVerticalGroup(
            jPanel94Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel94Layout.createSequentialGroup()
                .addComponent(jPanel98, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jb, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel95.setBackground(new java.awt.Color(248, 248, 248));
        jPanel95.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        jLabel76.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel76.setForeground(new java.awt.Color(188, 188, 188));
        jLabel76.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel76.setText("Jumlah Pelanggan");

        javax.swing.GroupLayout jPanel99Layout = new javax.swing.GroupLayout(jPanel99);
        jPanel99.setLayout(jPanel99Layout);
        jPanel99Layout.setHorizontalGroup(
            jPanel99Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel76, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
        );
        jPanel99Layout.setVerticalGroup(
            jPanel99Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel76, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        jpel.setFont(new java.awt.Font("SansSerif", 1, 48)); // NOI18N
        jpel.setForeground(new java.awt.Color(25, 181, 254));
        jpel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpel.setText("x");

        javax.swing.GroupLayout jPanel95Layout = new javax.swing.GroupLayout(jPanel95);
        jPanel95.setLayout(jPanel95Layout);
        jPanel95Layout.setHorizontalGroup(
            jPanel95Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel99, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jpel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel95Layout.setVerticalGroup(
            jPanel95Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel95Layout.createSequentialGroup()
                .addComponent(jPanel99, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel96.setBackground(new java.awt.Color(248, 248, 248));
        jPanel96.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        jLabel80.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel80.setForeground(new java.awt.Color(188, 188, 188));
        jLabel80.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel80.setText("Jumlah Supplier");

        javax.swing.GroupLayout jPanel100Layout = new javax.swing.GroupLayout(jPanel100);
        jPanel100.setLayout(jPanel100Layout);
        jPanel100Layout.setHorizontalGroup(
            jPanel100Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel80, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
        );
        jPanel100Layout.setVerticalGroup(
            jPanel100Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel80, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        jsup.setFont(new java.awt.Font("SansSerif", 1, 48)); // NOI18N
        jsup.setForeground(new java.awt.Color(25, 181, 254));
        jsup.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jsup.setText("x");

        javax.swing.GroupLayout jPanel96Layout = new javax.swing.GroupLayout(jPanel96);
        jPanel96.setLayout(jPanel96Layout);
        jPanel96Layout.setHorizontalGroup(
            jPanel96Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel100, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jsup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel96Layout.setVerticalGroup(
            jPanel96Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel96Layout.createSequentialGroup()
                .addComponent(jPanel100, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jsup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel97.setBackground(new java.awt.Color(248, 248, 248));
        jPanel97.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        jLabel83.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel83.setForeground(new java.awt.Color(188, 188, 188));
        jLabel83.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel83.setText("Jumlah Merk");

        javax.swing.GroupLayout jPanel101Layout = new javax.swing.GroupLayout(jPanel101);
        jPanel101.setLayout(jPanel101Layout);
        jPanel101Layout.setHorizontalGroup(
            jPanel101Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel83, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
        );
        jPanel101Layout.setVerticalGroup(
            jPanel101Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel83, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        jm.setFont(new java.awt.Font("SansSerif", 1, 48)); // NOI18N
        jm.setForeground(new java.awt.Color(25, 181, 254));
        jm.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jm.setText("x");

        javax.swing.GroupLayout jPanel97Layout = new javax.swing.GroupLayout(jPanel97);
        jPanel97.setLayout(jPanel97Layout);
        jPanel97Layout.setHorizontalGroup(
            jPanel97Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel101, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel97Layout.setVerticalGroup(
            jPanel97Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel97Layout.createSequentialGroup()
                .addComponent(jPanel101, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel87.setBackground(new java.awt.Color(248, 248, 248));
        jPanel87.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        jLabel86.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel86.setForeground(new java.awt.Color(188, 188, 188));
        jLabel86.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel86.setText("Jumlah Satuan");

        javax.swing.GroupLayout jPanel102Layout = new javax.swing.GroupLayout(jPanel102);
        jPanel102.setLayout(jPanel102Layout);
        jPanel102Layout.setHorizontalGroup(
            jPanel102Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel86, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
        );
        jPanel102Layout.setVerticalGroup(
            jPanel102Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel86, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        jsat.setFont(new java.awt.Font("SansSerif", 1, 48)); // NOI18N
        jsat.setForeground(new java.awt.Color(25, 181, 254));
        jsat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jsat.setText("x");

        javax.swing.GroupLayout jPanel87Layout = new javax.swing.GroupLayout(jPanel87);
        jPanel87.setLayout(jPanel87Layout);
        jPanel87Layout.setHorizontalGroup(
            jPanel87Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel102, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jsat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel87Layout.setVerticalGroup(
            jPanel87Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel87Layout.createSequentialGroup()
                .addComponent(jPanel102, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jsat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel88.setBackground(new java.awt.Color(248, 248, 248));
        jPanel88.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        jLabel87.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel87.setForeground(new java.awt.Color(188, 188, 188));
        jLabel87.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel87.setText("Jumlah Pegawai");

        javax.swing.GroupLayout jPanel103Layout = new javax.swing.GroupLayout(jPanel103);
        jPanel103.setLayout(jPanel103Layout);
        jPanel103Layout.setHorizontalGroup(
            jPanel103Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel87, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
        );
        jPanel103Layout.setVerticalGroup(
            jPanel103Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel87, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        jpeg.setFont(new java.awt.Font("SansSerif", 1, 48)); // NOI18N
        jpeg.setForeground(new java.awt.Color(25, 181, 254));
        jpeg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpeg.setText("x");

        javax.swing.GroupLayout jPanel88Layout = new javax.swing.GroupLayout(jPanel88);
        jPanel88.setLayout(jPanel88Layout);
        jPanel88Layout.setHorizontalGroup(
            jPanel88Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel103, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jpeg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel88Layout.setVerticalGroup(
            jPanel88Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel88Layout.createSequentialGroup()
                .addComponent(jPanel103, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpeg, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/storeset.png"))); // NOI18N
        jLabel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        jLabel78.setFont(new java.awt.Font("SansSerif", 1, 48)); // NOI18N
        jLabel78.setForeground(new java.awt.Color(188, 188, 188));
        jLabel78.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel78.setText("TOKO");
        jLabel78.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        jLabel79.setFont(new java.awt.Font("SansSerif", 1, 48)); // NOI18N
        jLabel79.setForeground(new java.awt.Color(25, 181, 254));
        jLabel79.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel79.setText("ABADI JAYA");
        jLabel79.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        jLabel81.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel81.setForeground(new java.awt.Color(188, 188, 188));
        jLabel81.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel81.setText("Jl. Raya Kenangan Indah RT 001 RW 001 Kecamatan Cipayung Kota Depok");
        jLabel81.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        javax.swing.GroupLayout dashAdminLayout = new javax.swing.GroupLayout(dashAdmin);
        dashAdmin.setLayout(dashAdminLayout);
        dashAdminLayout.setHorizontalGroup(
            dashAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel75, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(dashAdminLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dashAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel78, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dashAdminLayout.createSequentialGroup()
                        .addComponent(jPanel94, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel95, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel96, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel97, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel87, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel88, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dashAdminLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(dashAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dashAdminLayout.createSequentialGroup()
                                .addGroup(dashAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel78, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel79, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addComponent(jPanel89, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel81, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        dashAdminLayout.setVerticalGroup(
            dashAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashAdminLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel75, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dashAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(dashAdminLayout.createSequentialGroup()
                        .addGroup(dashAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dashAdminLayout.createSequentialGroup()
                                .addComponent(jLabel78, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel89, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel81, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel78, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dashAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dashAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel94, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel95, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel96, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel97, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel87, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel88, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        mainpane.add(dashAdmin, "card2");

        dashGudang.setBackground(new java.awt.Color(248, 248, 248));

        jPanel68.setBackground(new java.awt.Color(235, 248, 252));

        jLabel9.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(25, 181, 254));
        jLabel9.setText("Selamat Datang,");

        ptgsName.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        ptgsName.setForeground(new java.awt.Color(25, 181, 254));
        ptgsName.setText("$Name !");

        javax.swing.GroupLayout jPanel68Layout = new javax.swing.GroupLayout(jPanel68);
        jPanel68.setLayout(jPanel68Layout);
        jPanel68Layout.setHorizontalGroup(
            jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel68Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ptgsName, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel68Layout.setVerticalGroup(
            jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel68Layout.createSequentialGroup()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(ptgsName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jLabel71.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel71.setForeground(new java.awt.Color(102, 102, 102));
        jLabel71.setText("Berikut adalah daftar barang yang harus segera di Re-Stok :");

        jScrollPane8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        tStok.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tStok.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tStok.setGridColor(new java.awt.Color(255, 255, 255));
        tStok.setRowHeight(25);
        tStok.setSelectionBackground(new java.awt.Color(25, 181, 254));
        jScrollPane8.setViewportView(tStok);

        jPanel74.setBackground(new java.awt.Color(235, 248, 252));

        jLabel77.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel77.setForeground(new java.awt.Color(25, 181, 254));
        jLabel77.setText("Jumlah Barang :");

        jml_stok.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jml_stok.setForeground(new java.awt.Color(25, 181, 254));
        jml_stok.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jml_stok.setText("x");

        javax.swing.GroupLayout jPanel74Layout = new javax.swing.GroupLayout(jPanel74);
        jPanel74.setLayout(jPanel74Layout);
        jPanel74Layout.setHorizontalGroup(
            jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel74Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jml_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel74Layout.setVerticalGroup(
            jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel77, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
            .addComponent(jml_stok, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel76.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel76Layout = new javax.swing.GroupLayout(jPanel76);
        jPanel76.setLayout(jPanel76Layout);
        jPanel76Layout.setHorizontalGroup(
            jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel76Layout.setVerticalGroup(
            jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel69.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel69Layout = new javax.swing.GroupLayout(jPanel69);
        jPanel69.setLayout(jPanel69Layout);
        jPanel69Layout.setHorizontalGroup(
            jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );
        jPanel69Layout.setVerticalGroup(
            jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel74.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel74.setText("Stok Limit :");

        tflimit.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tflimit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tflimit.setText("20");
        tflimit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tflimitKeyReleased(evt);
            }
        });

        btnCetak.setBackground(new java.awt.Color(25, 181, 254));
        btnCetak.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        bCetak.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        bCetak.setForeground(new java.awt.Color(255, 255, 255));
        bCetak.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/print.png"))); // NOI18N
        bCetak.setText("Cetak");
        bCetak.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bCetak.setIconTextGap(12);
        bCetak.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bCetakMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bCetakMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bCetakMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bCetakMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                bCetakMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout btnCetakLayout = new javax.swing.GroupLayout(btnCetak);
        btnCetak.setLayout(btnCetakLayout);
        btnCetakLayout.setHorizontalGroup(
            btnCetakLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        btnCetakLayout.setVerticalGroup(
            btnCetakLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout dashGudangLayout = new javax.swing.GroupLayout(dashGudang);
        dashGudang.setLayout(dashGudangLayout);
        dashGudangLayout.setHorizontalGroup(
            dashGudangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel68, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(dashGudangLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dashGudangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dashGudangLayout.createSequentialGroup()
                        .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(184, 184, 184)
                        .addComponent(jPanel69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tflimit, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCetak, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel76, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel74, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        dashGudangLayout.setVerticalGroup(
            dashGudangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashGudangLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel68, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dashGudangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel69, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tflimit)
                    .addComponent(jLabel74, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel71, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCetak, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel76, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel74, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        mainpane.add(dashGudang, "card3");

        dashKasir.setBackground(new java.awt.Color(248, 248, 248));

        jPanel92.setBackground(new java.awt.Color(235, 248, 252));

        jLabel98.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel98.setForeground(new java.awt.Color(25, 181, 254));
        jLabel98.setText("Selamat Datang,");

        ptgsName1.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        ptgsName1.setForeground(new java.awt.Color(25, 181, 254));
        ptgsName1.setText("$Name !");

        javax.swing.GroupLayout jPanel92Layout = new javax.swing.GroupLayout(jPanel92);
        jPanel92.setLayout(jPanel92Layout);
        jPanel92Layout.setHorizontalGroup(
            jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel92Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel98)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ptgsName1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 571, Short.MAX_VALUE))
        );
        jPanel92Layout.setVerticalGroup(
            jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel92Layout.createSequentialGroup()
                .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(ptgsName1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jButton9.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jButton9.setForeground(new java.awt.Color(51, 51, 51));
        jButton9.setText("Open POS");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dashKasirLayout = new javax.swing.GroupLayout(dashKasir);
        dashKasir.setLayout(dashKasirLayout);
        dashKasirLayout.setHorizontalGroup(
            dashKasirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel92, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dashKasirLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(404, 404, 404))
        );
        dashKasirLayout.setVerticalGroup(
            dashKasirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashKasirLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel92, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 266, Short.MAX_VALUE)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(267, 267, 267))
        );

        mainpane.add(dashKasir, "card4");

        master.setBackground(new java.awt.Color(248, 248, 248));

        bg1.setBackground(new java.awt.Color(239, 239, 239));

        barang.setBackground(new java.awt.Color(255, 255, 255));
        barang.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        barang.setPreferredSize(new java.awt.Dimension(200, 200));
        barang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                barangMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                barangMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                barangMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                barangMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                barangMouseReleased(evt);
            }
        });

        iconBarang.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconBarang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/barang_g.png"))); // NOI18N

        lb_barang.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        lb_barang.setForeground(new java.awt.Color(188, 188, 188));
        lb_barang.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_barang.setText("Data Barang");
        lb_barang.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout barangLayout = new javax.swing.GroupLayout(barang);
        barang.setLayout(barangLayout);
        barangLayout.setHorizontalGroup(
            barangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iconBarang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lb_barang, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
        );
        barangLayout.setVerticalGroup(
            barangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(barangLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(iconBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lb_barang, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout bg1Layout = new javax.swing.GroupLayout(bg1);
        bg1.setLayout(bg1Layout);
        bg1Layout.setHorizontalGroup(
            bg1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bg1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(barang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        bg1Layout.setVerticalGroup(
            bg1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bg1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(barang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bg3.setBackground(new java.awt.Color(239, 239, 239));

        pegawai.setBackground(new java.awt.Color(255, 255, 255));
        pegawai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pegawai.setPreferredSize(new java.awt.Dimension(200, 200));
        pegawai.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pegawaiMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pegawaiMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pegawaiMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pegawaiMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pegawaiMouseReleased(evt);
            }
        });

        iconPegawai.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconPegawai.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pegawai_g.png"))); // NOI18N

        lb_pegawai.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        lb_pegawai.setForeground(new java.awt.Color(188, 188, 188));
        lb_pegawai.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_pegawai.setText("Data Pegawai");
        lb_pegawai.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout pegawaiLayout = new javax.swing.GroupLayout(pegawai);
        pegawai.setLayout(pegawaiLayout);
        pegawaiLayout.setHorizontalGroup(
            pegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iconPegawai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lb_pegawai, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
        );
        pegawaiLayout.setVerticalGroup(
            pegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pegawaiLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(iconPegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lb_pegawai, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout bg3Layout = new javax.swing.GroupLayout(bg3);
        bg3.setLayout(bg3Layout);
        bg3Layout.setHorizontalGroup(
            bg3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bg3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pegawai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        bg3Layout.setVerticalGroup(
            bg3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bg3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pegawai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bg4.setBackground(new java.awt.Color(239, 239, 239));

        merk.setBackground(new java.awt.Color(255, 255, 255));
        merk.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        merk.setPreferredSize(new java.awt.Dimension(200, 200));
        merk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                merkMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                merkMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                merkMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                merkMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                merkMouseReleased(evt);
            }
        });

        iconMerk.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconMerk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/merk_g.png"))); // NOI18N

        lb_merk.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        lb_merk.setForeground(new java.awt.Color(188, 188, 188));
        lb_merk.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_merk.setText("Data Merk");
        lb_merk.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout merkLayout = new javax.swing.GroupLayout(merk);
        merk.setLayout(merkLayout);
        merkLayout.setHorizontalGroup(
            merkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iconMerk, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
            .addComponent(lb_merk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        merkLayout.setVerticalGroup(
            merkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(merkLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(iconMerk, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lb_merk, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout bg4Layout = new javax.swing.GroupLayout(bg4);
        bg4.setLayout(bg4Layout);
        bg4Layout.setHorizontalGroup(
            bg4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bg4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(merk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        bg4Layout.setVerticalGroup(
            bg4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bg4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(merk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bg5.setBackground(new java.awt.Color(239, 239, 239));

        satuan.setBackground(new java.awt.Color(255, 255, 255));
        satuan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        satuan.setPreferredSize(new java.awt.Dimension(200, 200));
        satuan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                satuanMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                satuanMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                satuanMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                satuanMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                satuanMouseReleased(evt);
            }
        });

        iconSatuan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconSatuan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/satuan_g.png"))); // NOI18N

        lb_satuan.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        lb_satuan.setForeground(new java.awt.Color(188, 188, 188));
        lb_satuan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_satuan.setText("Data Satuan");
        lb_satuan.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout satuanLayout = new javax.swing.GroupLayout(satuan);
        satuan.setLayout(satuanLayout);
        satuanLayout.setHorizontalGroup(
            satuanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iconSatuan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lb_satuan, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
        );
        satuanLayout.setVerticalGroup(
            satuanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(satuanLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(iconSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lb_satuan, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout bg5Layout = new javax.swing.GroupLayout(bg5);
        bg5.setLayout(bg5Layout);
        bg5Layout.setHorizontalGroup(
            bg5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bg5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(satuan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        bg5Layout.setVerticalGroup(
            bg5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bg5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(satuan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bg6.setBackground(new java.awt.Color(239, 239, 239));

        supplier.setBackground(new java.awt.Color(255, 255, 255));
        supplier.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        supplier.setPreferredSize(new java.awt.Dimension(200, 200));
        supplier.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                supplierMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                supplierMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                supplierMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                supplierMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                supplierMouseReleased(evt);
            }
        });

        iconSupplier.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconSupplier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/supplier_g.png"))); // NOI18N

        lb_supplier.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        lb_supplier.setForeground(new java.awt.Color(188, 188, 188));
        lb_supplier.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_supplier.setText("Data Supplier");
        lb_supplier.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout supplierLayout = new javax.swing.GroupLayout(supplier);
        supplier.setLayout(supplierLayout);
        supplierLayout.setHorizontalGroup(
            supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iconSupplier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lb_supplier, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
        );
        supplierLayout.setVerticalGroup(
            supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(supplierLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(iconSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lb_supplier, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout bg6Layout = new javax.swing.GroupLayout(bg6);
        bg6.setLayout(bg6Layout);
        bg6Layout.setHorizontalGroup(
            bg6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bg6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(supplier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        bg6Layout.setVerticalGroup(
            bg6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bg6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(supplier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bg2.setBackground(new java.awt.Color(239, 239, 239));

        pelanggan.setBackground(new java.awt.Color(255, 255, 255));
        pelanggan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pelanggan.setPreferredSize(new java.awt.Dimension(200, 200));
        pelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pelangganMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pelangganMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pelangganMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pelangganMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pelangganMouseReleased(evt);
            }
        });

        iconPelanggan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconPelanggan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pelanggan_g.png"))); // NOI18N

        lb_pelanggan.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        lb_pelanggan.setForeground(new java.awt.Color(188, 188, 188));
        lb_pelanggan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_pelanggan.setText("Data Pelanggan");
        lb_pelanggan.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout pelangganLayout = new javax.swing.GroupLayout(pelanggan);
        pelanggan.setLayout(pelangganLayout);
        pelangganLayout.setHorizontalGroup(
            pelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iconPelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lb_pelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
        );
        pelangganLayout.setVerticalGroup(
            pelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pelangganLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(iconPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lb_pelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout bg2Layout = new javax.swing.GroupLayout(bg2);
        bg2.setLayout(bg2Layout);
        bg2Layout.setHorizontalGroup(
            bg2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bg2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        bg2Layout.setVerticalGroup(
            bg2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bg2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout masterLayout = new javax.swing.GroupLayout(master);
        master.setLayout(masterLayout);
        masterLayout.setHorizontalGroup(
            masterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(masterLayout.createSequentialGroup()
                .addGap(123, 123, 123)
                .addGroup(masterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bg1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bg4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(81, 81, 81)
                .addGroup(masterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bg2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bg5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(79, 79, 79)
                .addGroup(masterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bg3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bg6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        masterLayout.setVerticalGroup(
            masterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(masterLayout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addGroup(masterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bg2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bg1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bg6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(67, 67, 67)
                .addGroup(masterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bg4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bg5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bg3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        mainpane.add(master, "card5");

        master_barang.setBackground(new java.awt.Color(248, 248, 248));
        master_barang.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel36.setBackground(new java.awt.Color(235, 248, 252));

        jLabel50.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel50.setForeground(new java.awt.Color(25, 181, 254));
        jLabel50.setText("Data Barang");

        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/closeb.png"))); // NOI18N
        jLabel51.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel51.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel51MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 509, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 501, Short.MAX_VALUE)
                .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
            .addComponent(jLabel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        master_barang.add(jPanel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 11, 1060, -1));

        jPanel37.setBackground(new java.awt.Color(255, 255, 255));
        jPanel37.setLayout(new java.awt.CardLayout());

        jPanel38.setBackground(new java.awt.Color(255, 255, 255));

        btnTambah5.setBackground(new java.awt.Color(25, 181, 254));
        btnTambah5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTambah5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnTambah5MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnTambah5MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnTambah5MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnTambah5MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnTambah5MouseReleased(evt);
            }
        });

        jLabel52.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel52.setForeground(new java.awt.Color(255, 255, 255));
        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add.png"))); // NOI18N
        jLabel52.setText("Tambah Barang");

        javax.swing.GroupLayout btnTambah5Layout = new javax.swing.GroupLayout(btnTambah5);
        btnTambah5.setLayout(btnTambah5Layout);
        btnTambah5Layout.setHorizontalGroup(
            btnTambah5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel52, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );
        btnTambah5Layout.setVerticalGroup(
            btnTambah5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnUbah5.setBackground(new java.awt.Color(33, 191, 115));
        btnUbah5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUbah5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUbah5MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnUbah5MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnUbah5MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnUbah5MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnUbah5MouseReleased(evt);
            }
        });

        jLabel53.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(255, 255, 255));
        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ubah.png"))); // NOI18N
        jLabel53.setText("Ubah");
        jLabel53.setIconTextGap(8);

        javax.swing.GroupLayout btnUbah5Layout = new javax.swing.GroupLayout(btnUbah5);
        btnUbah5.setLayout(btnUbah5Layout);
        btnUbah5Layout.setHorizontalGroup(
            btnUbah5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel53, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btnUbah5Layout.setVerticalGroup(
            btnUbah5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnHapus5.setBackground(new java.awt.Color(255, 46, 49));
        btnHapus5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHapus5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnHapus5MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnHapus5MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnHapus5MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnHapus5MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnHapus5MouseReleased(evt);
            }
        });

        jLabel54.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel54.setForeground(new java.awt.Color(255, 255, 255));
        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/hapus.png"))); // NOI18N
        jLabel54.setText("Hapus");
        jLabel54.setIconTextGap(8);

        javax.swing.GroupLayout btnHapus5Layout = new javax.swing.GroupLayout(btnHapus5);
        btnHapus5.setLayout(btnHapus5Layout);
        btnHapus5Layout.setHorizontalGroup(
            btnHapus5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel54, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btnHapus5Layout.setVerticalGroup(
            btnHapus5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panelSearch5.setBackground(new java.awt.Color(255, 255, 255));
        panelSearch5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));
        panelSearch5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tfcari5.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfcari5.setBorder(null);
        tfcari5.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfcari5FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfcari5FocusLost(evt);
            }
        });
        tfcari5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfcari5KeyReleased(evt);
            }
        });
        panelSearch5.add(tfcari5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 550, 20));

        jPanel9.setBackground(new java.awt.Color(25, 181, 254));
        jPanel9.setLayout(new java.awt.BorderLayout());

        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel58.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cari.png"))); // NOI18N
        jPanel9.add(jLabel58, java.awt.BorderLayout.CENTER);

        panelSearch5.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 0, 40, 40));

        jPanel39.setLayout(new java.awt.BorderLayout());

        jScrollPane6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        tBarang.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tBarang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No", "ID", "Nama Barang", "Merk", "Satuan", "stok", "Harga Beli Satuan", "Harga Beli Pcs", "Harga Jual Satuan", "Harga Jual Pcs"
            }
        ));
        tBarang.setGridColor(new java.awt.Color(255, 255, 255));
        tBarang.setRowHeight(25);
        tBarang.setSelectionBackground(new java.awt.Color(25, 181, 254));
        jScrollPane6.setViewportView(tBarang);

        jPanel39.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        jPanel40.setBackground(new java.awt.Color(235, 248, 252));

        jLabel56.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(25, 181, 254));
        jLabel56.setText("Hasil Pencarian :");

        jLabel57.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(25, 181, 254));
        jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel57.setText("Jumlah Barang :");

        jml_brg.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jml_brg.setForeground(new java.awt.Color(25, 181, 254));
        jml_brg.setText("x");

        sum_cari5.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        sum_cari5.setForeground(new java.awt.Color(25, 181, 254));
        sum_cari5.setText("0");

        javax.swing.GroupLayout jPanel40Layout = new javax.swing.GroupLayout(jPanel40);
        jPanel40.setLayout(jPanel40Layout);
        jPanel40Layout.setHorizontalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sum_cari5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jml_brg, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel40Layout.setVerticalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel56, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
            .addComponent(jLabel57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jml_brg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(sum_cari5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel38Layout = new javax.swing.GroupLayout(jPanel38);
        jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel38Layout.createSequentialGroup()
                        .addComponent(btnTambah5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUbah5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelSearch5, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel38Layout.setVerticalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnHapus5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUbah5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTambah5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelSearch5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel39, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel37.add(jPanel38, "card2");

        master_barang.add(jPanel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 61, 1045, 605));

        mainpane.add(master_barang, "card11");

        master_pelanggan.setBackground(new java.awt.Color(248, 248, 248));
        master_pelanggan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel16.setBackground(new java.awt.Color(235, 248, 252));

        jLabel14.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(25, 181, 254));
        jLabel14.setText("Data Pelanggan");

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/closeb.png"))); // NOI18N
        jLabel16.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel16MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 509, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 501, Short.MAX_VALUE)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        master_pelanggan.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 11, 1060, -1));

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));
        jPanel17.setLayout(new java.awt.CardLayout());

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));

        btnTambah1.setBackground(new java.awt.Color(25, 181, 254));
        btnTambah1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTambah1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnTambah1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnTambah1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnTambah1MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnTambah1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnTambah1MouseReleased(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add.png"))); // NOI18N
        jLabel17.setText("Tambah Pelanggan");

        javax.swing.GroupLayout btnTambah1Layout = new javax.swing.GroupLayout(btnTambah1);
        btnTambah1.setLayout(btnTambah1Layout);
        btnTambah1Layout.setHorizontalGroup(
            btnTambah1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );
        btnTambah1Layout.setVerticalGroup(
            btnTambah1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnUbah1.setBackground(new java.awt.Color(33, 191, 115));
        btnUbah1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUbah1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUbah1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnUbah1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnUbah1MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnUbah1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnUbah1MouseReleased(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ubah.png"))); // NOI18N
        jLabel19.setText("Ubah");
        jLabel19.setIconTextGap(8);

        javax.swing.GroupLayout btnUbah1Layout = new javax.swing.GroupLayout(btnUbah1);
        btnUbah1.setLayout(btnUbah1Layout);
        btnUbah1Layout.setHorizontalGroup(
            btnUbah1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btnUbah1Layout.setVerticalGroup(
            btnUbah1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnHapus1.setBackground(new java.awt.Color(255, 46, 49));
        btnHapus1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHapus1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnHapus1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnHapus1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnHapus1MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnHapus1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnHapus1MouseReleased(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/hapus.png"))); // NOI18N
        jLabel21.setText("Hapus");
        jLabel21.setIconTextGap(8);

        javax.swing.GroupLayout btnHapus1Layout = new javax.swing.GroupLayout(btnHapus1);
        btnHapus1.setLayout(btnHapus1Layout);
        btnHapus1Layout.setHorizontalGroup(
            btnHapus1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btnHapus1Layout.setVerticalGroup(
            btnHapus1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panelSearch1.setBackground(new java.awt.Color(255, 255, 255));
        panelSearch1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));
        panelSearch1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tfcari1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfcari1.setBorder(null);
        tfcari1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfcari1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfcari1FocusLost(evt);
            }
        });
        tfcari1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfcari1KeyReleased(evt);
            }
        });
        panelSearch1.add(tfcari1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 550, 20));

        jPanel12.setBackground(new java.awt.Color(25, 181, 254));
        jPanel12.setLayout(new java.awt.BorderLayout());

        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel55.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cari.png"))); // NOI18N
        jPanel12.add(jLabel55, java.awt.BorderLayout.CENTER);

        panelSearch1.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 0, 40, 40));

        jPanel21.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        tbl_plg.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tbl_plg.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "No", "ID", "Nama Pelanggan", "Alamat", "No. Tlp"
            }
        ));
        tbl_plg.setGridColor(new java.awt.Color(255, 255, 255));
        tbl_plg.setRowHeight(25);
        tbl_plg.setSelectionBackground(new java.awt.Color(25, 181, 254));
        jScrollPane2.setViewportView(tbl_plg);

        jPanel21.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel22.setBackground(new java.awt.Color(235, 248, 252));

        jLabel24.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(25, 181, 254));
        jLabel24.setText("Hasil Pencarian :");

        jLabel25.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(25, 181, 254));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Jumlah Pelanggan :");

        jml_plg.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jml_plg.setForeground(new java.awt.Color(25, 181, 254));
        jml_plg.setText("x");

        sum_cari1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        sum_cari1.setForeground(new java.awt.Color(25, 181, 254));
        sum_cari1.setText("0");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sum_cari1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jml_plg, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
            .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jml_plg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(sum_cari1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(btnTambah1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUbah1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnHapus1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUbah1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTambah1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelSearch1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel17.add(jPanel18, "card2");

        master_pelanggan.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 61, 1045, 605));

        mainpane.add(master_pelanggan, "card11");

        master_supplier.setBackground(new java.awt.Color(248, 248, 248));
        master_supplier.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel19.setBackground(new java.awt.Color(235, 248, 252));

        jLabel26.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(25, 181, 254));
        jLabel26.setText("Data Supplier");

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/closeb.png"))); // NOI18N
        jLabel27.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel27MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 509, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 501, Short.MAX_VALUE)
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
            .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        master_supplier.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 11, 1060, -1));

        jPanel20.setBackground(new java.awt.Color(255, 255, 255));
        jPanel20.setLayout(new java.awt.CardLayout());

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));

        btnTambah2.setBackground(new java.awt.Color(25, 181, 254));
        btnTambah2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTambah2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnTambah2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnTambah2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnTambah2MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnTambah2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnTambah2MouseReleased(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add.png"))); // NOI18N
        jLabel28.setText("Tambah Supplier");

        javax.swing.GroupLayout btnTambah2Layout = new javax.swing.GroupLayout(btnTambah2);
        btnTambah2.setLayout(btnTambah2Layout);
        btnTambah2Layout.setHorizontalGroup(
            btnTambah2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );
        btnTambah2Layout.setVerticalGroup(
            btnTambah2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnUbah2.setBackground(new java.awt.Color(33, 191, 115));
        btnUbah2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUbah2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUbah2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnUbah2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnUbah2MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnUbah2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnUbah2MouseReleased(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ubah.png"))); // NOI18N
        jLabel29.setText("Ubah");
        jLabel29.setIconTextGap(8);

        javax.swing.GroupLayout btnUbah2Layout = new javax.swing.GroupLayout(btnUbah2);
        btnUbah2.setLayout(btnUbah2Layout);
        btnUbah2Layout.setHorizontalGroup(
            btnUbah2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btnUbah2Layout.setVerticalGroup(
            btnUbah2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnHapus2.setBackground(new java.awt.Color(255, 46, 49));
        btnHapus2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHapus2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnHapus2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnHapus2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnHapus2MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnHapus2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnHapus2MouseReleased(evt);
            }
        });

        jLabel30.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/hapus.png"))); // NOI18N
        jLabel30.setText("Hapus");
        jLabel30.setIconTextGap(8);

        javax.swing.GroupLayout btnHapus2Layout = new javax.swing.GroupLayout(btnHapus2);
        btnHapus2.setLayout(btnHapus2Layout);
        btnHapus2Layout.setHorizontalGroup(
            btnHapus2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btnHapus2Layout.setVerticalGroup(
            btnHapus2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panelSearch2.setBackground(new java.awt.Color(255, 255, 255));
        panelSearch2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));
        panelSearch2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tfcari2.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfcari2.setBorder(null);
        tfcari2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfcari2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfcari2FocusLost(evt);
            }
        });
        tfcari2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfcari2KeyReleased(evt);
            }
        });
        panelSearch2.add(tfcari2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 550, 20));

        jPanel41.setBackground(new java.awt.Color(25, 181, 254));
        jPanel41.setLayout(new java.awt.BorderLayout());

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cari.png"))); // NOI18N
        jPanel41.add(jLabel23, java.awt.BorderLayout.CENTER);

        panelSearch2.add(jPanel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 0, 40, 40));

        jPanel24.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        tbl_supp.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tbl_supp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "No", "ID", "Nama Supplier", "Alamat", "No. Tlp"
            }
        ));
        tbl_supp.setGridColor(new java.awt.Color(255, 255, 255));
        tbl_supp.setRowHeight(25);
        tbl_supp.setSelectionBackground(new java.awt.Color(25, 181, 254));
        jScrollPane3.setViewportView(tbl_supp);

        jPanel24.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jPanel25.setBackground(new java.awt.Color(235, 248, 252));

        jLabel32.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(25, 181, 254));
        jLabel32.setText("Hasil Pencarian :");

        jLabel33.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(25, 181, 254));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel33.setText("Jumlah Supplier :");

        jml_supp.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jml_supp.setForeground(new java.awt.Color(25, 181, 254));
        jml_supp.setText("x");

        sum_cari2.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        sum_cari2.setForeground(new java.awt.Color(25, 181, 254));
        sum_cari2.setText("0");

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sum_cari2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jml_supp, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
            .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jml_supp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(sum_cari2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(btnTambah2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUbah2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelSearch2, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnHapus2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUbah2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTambah2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelSearch2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel20.add(jPanel23, "card2");

        master_supplier.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 61, 1045, 605));

        mainpane.add(master_supplier, "card11");

        master_merk.setBackground(new java.awt.Color(248, 248, 248));
        master_merk.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel31.setBackground(new java.awt.Color(235, 248, 252));

        jLabel42.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(25, 181, 254));
        jLabel42.setText("Data Merk");

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/closeb.png"))); // NOI18N
        jLabel43.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel43.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel43MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 509, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 501, Short.MAX_VALUE)
                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
            .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        master_merk.add(jPanel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 11, 1060, -1));

        jPanel32.setBackground(new java.awt.Color(255, 255, 255));
        jPanel32.setLayout(new java.awt.CardLayout());

        jPanel33.setBackground(new java.awt.Color(255, 255, 255));

        btnTambah4.setBackground(new java.awt.Color(25, 181, 254));
        btnTambah4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTambah4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnTambah4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnTambah4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnTambah4MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnTambah4MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnTambah4MouseReleased(evt);
            }
        });

        jLabel44.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(255, 255, 255));
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add.png"))); // NOI18N
        jLabel44.setText("Tambah Merk");

        javax.swing.GroupLayout btnTambah4Layout = new javax.swing.GroupLayout(btnTambah4);
        btnTambah4.setLayout(btnTambah4Layout);
        btnTambah4Layout.setHorizontalGroup(
            btnTambah4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );
        btnTambah4Layout.setVerticalGroup(
            btnTambah4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnUbah4.setBackground(new java.awt.Color(33, 191, 115));
        btnUbah4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUbah4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUbah4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnUbah4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnUbah4MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnUbah4MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnUbah4MouseReleased(evt);
            }
        });

        jLabel45.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(255, 255, 255));
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ubah.png"))); // NOI18N
        jLabel45.setText("Ubah");
        jLabel45.setIconTextGap(8);

        javax.swing.GroupLayout btnUbah4Layout = new javax.swing.GroupLayout(btnUbah4);
        btnUbah4.setLayout(btnUbah4Layout);
        btnUbah4Layout.setHorizontalGroup(
            btnUbah4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel45, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btnUbah4Layout.setVerticalGroup(
            btnUbah4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnHapus4.setBackground(new java.awt.Color(255, 46, 49));
        btnHapus4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHapus4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnHapus4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnHapus4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnHapus4MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnHapus4MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnHapus4MouseReleased(evt);
            }
        });

        jLabel46.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(255, 255, 255));
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel46.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/hapus.png"))); // NOI18N
        jLabel46.setText("Hapus");
        jLabel46.setIconTextGap(8);

        javax.swing.GroupLayout btnHapus4Layout = new javax.swing.GroupLayout(btnHapus4);
        btnHapus4.setLayout(btnHapus4Layout);
        btnHapus4Layout.setHorizontalGroup(
            btnHapus4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btnHapus4Layout.setVerticalGroup(
            btnHapus4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panelSearch4.setBackground(new java.awt.Color(255, 255, 255));
        panelSearch4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));
        panelSearch4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tfcari4.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfcari4.setBorder(null);
        tfcari4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfcari4FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfcari4FocusLost(evt);
            }
        });
        tfcari4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfcari4KeyReleased(evt);
            }
        });
        panelSearch4.add(tfcari4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 550, 20));

        jPanel42.setBackground(new java.awt.Color(25, 181, 254));
        jPanel42.setLayout(new java.awt.BorderLayout());

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cari.png"))); // NOI18N
        jPanel42.add(jLabel31, java.awt.BorderLayout.CENTER);

        panelSearch4.add(jPanel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 0, 40, 40));

        jPanel34.setLayout(new java.awt.BorderLayout());

        jScrollPane5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        tbl_merk.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tbl_merk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "No", "ID", "Nama Merk"
            }
        ));
        tbl_merk.setGridColor(new java.awt.Color(255, 255, 255));
        tbl_merk.setRowHeight(25);
        tbl_merk.setSelectionBackground(new java.awt.Color(25, 181, 254));
        jScrollPane5.setViewportView(tbl_merk);

        jPanel34.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        jPanel35.setBackground(new java.awt.Color(235, 248, 252));

        jLabel48.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(25, 181, 254));
        jLabel48.setText("Hasil Pencarian :");

        jLabel49.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(25, 181, 254));
        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel49.setText("Jumlah Merk :");

        jml_merk.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jml_merk.setForeground(new java.awt.Color(25, 181, 254));
        jml_merk.setText("x");

        sum_cari4.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        sum_cari4.setForeground(new java.awt.Color(25, 181, 254));
        sum_cari4.setText("0");

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sum_cari4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jml_merk, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
            .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jml_merk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(sum_cari4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(btnTambah4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUbah4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelSearch4, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnHapus4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUbah4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTambah4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelSearch4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel34, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel32.add(jPanel33, "card2");

        master_merk.add(jPanel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 61, 1045, 605));

        mainpane.add(master_merk, "card11");

        master_satuan.setBackground(new java.awt.Color(248, 248, 248));
        master_satuan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel26.setBackground(new java.awt.Color(235, 248, 252));

        jLabel34.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(25, 181, 254));
        jLabel34.setText("Data Satuan");

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/closeb.png"))); // NOI18N
        jLabel35.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel35.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel35MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 509, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 501, Short.MAX_VALUE)
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
            .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        master_satuan.add(jPanel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 11, 1060, -1));

        jPanel27.setBackground(new java.awt.Color(255, 255, 255));
        jPanel27.setLayout(new java.awt.CardLayout());

        jPanel28.setBackground(new java.awt.Color(255, 255, 255));

        btnTambah3.setBackground(new java.awt.Color(25, 181, 254));
        btnTambah3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTambah3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnTambah3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnTambah3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnTambah3MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnTambah3MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnTambah3MouseReleased(evt);
            }
        });

        jLabel36.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add.png"))); // NOI18N
        jLabel36.setText("Tambah Satuan");

        javax.swing.GroupLayout btnTambah3Layout = new javax.swing.GroupLayout(btnTambah3);
        btnTambah3.setLayout(btnTambah3Layout);
        btnTambah3Layout.setHorizontalGroup(
            btnTambah3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );
        btnTambah3Layout.setVerticalGroup(
            btnTambah3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnUbah3.setBackground(new java.awt.Color(33, 191, 115));
        btnUbah3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUbah3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUbah3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnUbah3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnUbah3MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnUbah3MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnUbah3MouseReleased(evt);
            }
        });

        jLabel37.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ubah.png"))); // NOI18N
        jLabel37.setText("Ubah");
        jLabel37.setIconTextGap(8);

        javax.swing.GroupLayout btnUbah3Layout = new javax.swing.GroupLayout(btnUbah3);
        btnUbah3.setLayout(btnUbah3Layout);
        btnUbah3Layout.setHorizontalGroup(
            btnUbah3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btnUbah3Layout.setVerticalGroup(
            btnUbah3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnHapus3.setBackground(new java.awt.Color(255, 46, 49));
        btnHapus3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHapus3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnHapus3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnHapus3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnHapus3MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnHapus3MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnHapus3MouseReleased(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/hapus.png"))); // NOI18N
        jLabel38.setText("Hapus");
        jLabel38.setIconTextGap(8);

        javax.swing.GroupLayout btnHapus3Layout = new javax.swing.GroupLayout(btnHapus3);
        btnHapus3.setLayout(btnHapus3Layout);
        btnHapus3Layout.setHorizontalGroup(
            btnHapus3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btnHapus3Layout.setVerticalGroup(
            btnHapus3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panelSearch3.setBackground(new java.awt.Color(255, 255, 255));
        panelSearch3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));
        panelSearch3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tfcari3.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfcari3.setBorder(null);
        tfcari3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfcari3FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfcari3FocusLost(evt);
            }
        });
        tfcari3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfcari3KeyReleased(evt);
            }
        });
        panelSearch3.add(tfcari3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 550, 20));

        jPanel43.setBackground(new java.awt.Color(25, 181, 254));
        jPanel43.setLayout(new java.awt.BorderLayout());

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cari.png"))); // NOI18N
        jPanel43.add(jLabel47, java.awt.BorderLayout.CENTER);

        panelSearch3.add(jPanel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 0, 40, 40));

        jPanel29.setLayout(new java.awt.BorderLayout());

        jScrollPane4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        tbl_stn.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tbl_stn.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "No", "ID", "Nama Satuan", "Isi"
            }
        ));
        tbl_stn.setGridColor(new java.awt.Color(255, 255, 255));
        tbl_stn.setRowHeight(25);
        tbl_stn.setSelectionBackground(new java.awt.Color(25, 181, 254));
        jScrollPane4.setViewportView(tbl_stn);

        jPanel29.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        jPanel30.setBackground(new java.awt.Color(235, 248, 252));

        jLabel40.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(25, 181, 254));
        jLabel40.setText("Hasil Pencarian :");

        jLabel41.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(25, 181, 254));
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel41.setText("Jumlah Satuan :");

        jml_stn.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jml_stn.setForeground(new java.awt.Color(25, 181, 254));
        jml_stn.setText("x");

        sum_cari3.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        sum_cari3.setForeground(new java.awt.Color(25, 181, 254));
        sum_cari3.setText("0");

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sum_cari3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jml_stn, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
            .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jml_stn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(sum_cari3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addComponent(btnTambah3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUbah3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelSearch3, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnHapus3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUbah3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTambah3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelSearch3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel27.add(jPanel28, "card2");

        master_satuan.add(jPanel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 61, 1045, 605));

        mainpane.add(master_satuan, "card11");

        master_pegawai.setBackground(new java.awt.Color(248, 248, 248));
        master_pegawai.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(235, 248, 252));

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(25, 181, 254));
        jLabel1.setText("Data Pegawai");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/closeb.png"))); // NOI18N
        jLabel10.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 509, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 501, Short.MAX_VALUE)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        master_pegawai.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 11, 1060, -1));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.CardLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        btnTambah.setBackground(new java.awt.Color(25, 181, 254));
        btnTambah.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTambah.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnTambahMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnTambahMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnTambahMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnTambahMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnTambahMouseReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add.png"))); // NOI18N
        jLabel2.setText("Tambah Pegawai");

        javax.swing.GroupLayout btnTambahLayout = new javax.swing.GroupLayout(btnTambah);
        btnTambah.setLayout(btnTambahLayout);
        btnTambahLayout.setHorizontalGroup(
            btnTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );
        btnTambahLayout.setVerticalGroup(
            btnTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnUbah.setBackground(new java.awt.Color(33, 191, 115));
        btnUbah.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUbah.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUbahMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnUbahMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnUbahMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnUbahMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnUbahMouseReleased(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ubah.png"))); // NOI18N
        jLabel3.setText("Ubah");
        jLabel3.setIconTextGap(8);

        javax.swing.GroupLayout btnUbahLayout = new javax.swing.GroupLayout(btnUbah);
        btnUbah.setLayout(btnUbahLayout);
        btnUbahLayout.setHorizontalGroup(
            btnUbahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btnUbahLayout.setVerticalGroup(
            btnUbahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnHapus.setBackground(new java.awt.Color(255, 46, 49));
        btnHapus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHapus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnHapusMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnHapusMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnHapusMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnHapusMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnHapusMouseReleased(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/hapus.png"))); // NOI18N
        jLabel4.setText("Hapus");
        jLabel4.setIconTextGap(8);

        javax.swing.GroupLayout btnHapusLayout = new javax.swing.GroupLayout(btnHapus);
        btnHapus.setLayout(btnHapusLayout);
        btnHapusLayout.setHorizontalGroup(
            btnHapusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btnHapusLayout.setVerticalGroup(
            btnHapusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panelSearch.setBackground(new java.awt.Color(255, 255, 255));
        panelSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));
        panelSearch.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tfcari.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfcari.setBorder(null);
        tfcari.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfcariFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfcariFocusLost(evt);
            }
        });
        tfcari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfcariKeyReleased(evt);
            }
        });
        panelSearch.add(tfcari, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 550, 20));

        jPanel44.setBackground(new java.awt.Color(25, 181, 254));
        jPanel44.setLayout(new java.awt.BorderLayout());

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cari.png"))); // NOI18N
        jPanel44.add(jLabel39, java.awt.BorderLayout.CENTER);

        panelSearch.add(jPanel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 0, 40, 40));

        jPanel10.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        tbl_pgw.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tbl_pgw.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No", "ID", "Nama Prgawai", "Tanggal Lahir", "Jenis Kelamin", "Alamat", "No. Tlp", "Jabatan"
            }
        ));
        tbl_pgw.setGridColor(new java.awt.Color(255, 255, 255));
        tbl_pgw.setRowHeight(25);
        tbl_pgw.setSelectionBackground(new java.awt.Color(25, 181, 254));
        jScrollPane1.setViewportView(tbl_pgw);
        if (tbl_pgw.getColumnModel().getColumnCount() > 0) {
            tbl_pgw.getColumnModel().getColumn(3).setHeaderValue("Tanggal Lahir");
            tbl_pgw.getColumnModel().getColumn(4).setHeaderValue("Jenis Kelamin");
            tbl_pgw.getColumnModel().getColumn(7).setHeaderValue("Jabatan");
        }

        jPanel10.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel11.setBackground(new java.awt.Color(235, 248, 252));

        jLabel6.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(25, 181, 254));
        jLabel6.setText("Hasil Pencarian :");

        jLabel11.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(25, 181, 254));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Jumlah Pegawai :");

        jml_pgw.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jml_pgw.setForeground(new java.awt.Color(25, 181, 254));
        jml_pgw.setText("x");

        sum_cari.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        sum_cari.setForeground(new java.awt.Color(25, 181, 254));
        sum_cari.setText("0");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sum_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jml_pgw, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jml_pgw, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(sum_cari, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUbah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnHapus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUbah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTambah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.add(jPanel3, "card2");

        master_pegawai.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 61, 1050, 605));

        mainpane.add(master_pegawai, "card11");

        trans.setBackground(new java.awt.Color(248, 248, 248));

        bgjual.setBackground(new java.awt.Color(239, 239, 239));

        jual.setBackground(new java.awt.Color(255, 255, 255));
        jual.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jual.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jualMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jualMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jualMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jualMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jualMouseReleased(evt);
            }
        });

        iconSell.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconSell.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/sell_g.png"))); // NOI18N

        lb_sell.setFont(new java.awt.Font("SansSerif", 0, 20)); // NOI18N
        lb_sell.setForeground(new java.awt.Color(188, 188, 188));
        lb_sell.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_sell.setText("Transaksi Penjualan");

        javax.swing.GroupLayout jualLayout = new javax.swing.GroupLayout(jual);
        jual.setLayout(jualLayout);
        jualLayout.setHorizontalGroup(
            jualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iconSell, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lb_sell, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );
        jualLayout.setVerticalGroup(
            jualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jualLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(iconSell, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lb_sell, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 42, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout bgjualLayout = new javax.swing.GroupLayout(bgjual);
        bgjual.setLayout(bgjualLayout);
        bgjualLayout.setHorizontalGroup(
            bgjualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bgjualLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        bgjualLayout.setVerticalGroup(
            bgjualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bgjualLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bgbeli.setBackground(new java.awt.Color(239, 239, 239));

        beli.setBackground(new java.awt.Color(255, 255, 255));
        beli.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        beli.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                beliMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                beliMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                beliMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                beliMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                beliMouseReleased(evt);
            }
        });

        iconBuy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconBuy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/buy_g.png"))); // NOI18N

        lb_buy.setFont(new java.awt.Font("SansSerif", 0, 20)); // NOI18N
        lb_buy.setForeground(new java.awt.Color(188, 188, 188));
        lb_buy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_buy.setText("Transaksi Pembelian");

        javax.swing.GroupLayout beliLayout = new javax.swing.GroupLayout(beli);
        beli.setLayout(beliLayout);
        beliLayout.setHorizontalGroup(
            beliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iconBuy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lb_buy, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );
        beliLayout.setVerticalGroup(
            beliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(beliLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(iconBuy, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lb_buy, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 42, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout bgbeliLayout = new javax.swing.GroupLayout(bgbeli);
        bgbeli.setLayout(bgbeliLayout);
        bgbeliLayout.setHorizontalGroup(
            bgbeliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bgbeliLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(beli, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        bgbeliLayout.setVerticalGroup(
            bgbeliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bgbeliLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(beli, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout transLayout = new javax.swing.GroupLayout(trans);
        trans.setLayout(transLayout);
        transLayout.setHorizontalGroup(
            transLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, transLayout.createSequentialGroup()
                .addContainerGap(151, Short.MAX_VALUE)
                .addComponent(bgjual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(149, 149, 149)
                .addComponent(bgbeli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(152, 152, 152))
        );
        transLayout.setVerticalGroup(
            transLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, transLayout.createSequentialGroup()
                .addContainerGap(182, Short.MAX_VALUE)
                .addGroup(transLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bgbeli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bgjual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(185, 185, 185))
        );

        mainpane.add(trans, "card12");

        lap.setBackground(new java.awt.Color(248, 248, 248));

        jPanel4.setLayout(new java.awt.CardLayout());

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jPanel5.setBackground(new java.awt.Color(235, 248, 252));

        jLabel8.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(25, 181, 254));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Laporan Penjualan");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)), " Harian ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 1, 12), new java.awt.Color(188, 188, 188))); // NOI18N

        jPanel8.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jButton1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jButton1.setText("Cetak");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tgl4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(tgl4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)), " Bulanan ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 1, 12), new java.awt.Color(188, 188, 188))); // NOI18N

        jPanel14.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jButton2.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jButton2.setText("Cetak");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        cbBulan.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        cbBulan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember" }));

        tahun.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tahun.setForeground(new java.awt.Color(25, 181, 254));
        tahun.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tahun.setText("tahun");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(cbBulan, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tahun, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbBulan)
                            .addComponent(tahun, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));
        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)), " Tahunan ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 1, 12), new java.awt.Color(188, 188, 188))); // NOI18N

        jPanel45.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel45Layout = new javax.swing.GroupLayout(jPanel45);
        jPanel45.setLayout(jPanel45Layout);
        jPanel45Layout.setHorizontalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );
        jPanel45Layout.setVerticalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jButton3.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jButton3.setText("Cetak");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        tftahun.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tftahun.setForeground(new java.awt.Color(25, 181, 254));
        tftahun.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tftahun.setBorder(null);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tftahun))
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                        .addComponent(tftahun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 21, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel6, "card2");

        jPanel46.setLayout(new java.awt.CardLayout());

        jPanel47.setBackground(new java.awt.Color(255, 255, 255));

        jPanel48.setBackground(new java.awt.Color(235, 248, 252));

        jLabel13.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(25, 181, 254));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Laporan Pembelian");

        javax.swing.GroupLayout jPanel48Layout = new javax.swing.GroupLayout(jPanel48);
        jPanel48.setLayout(jPanel48Layout);
        jPanel48Layout.setHorizontalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel48Layout.setVerticalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );

        jPanel49.setBackground(new java.awt.Color(255, 255, 255));
        jPanel49.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)), " Harian ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 1, 12), new java.awt.Color(188, 188, 188))); // NOI18N

        jPanel50.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel50Layout = new javax.swing.GroupLayout(jPanel50);
        jPanel50.setLayout(jPanel50Layout);
        jPanel50Layout.setHorizontalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );
        jPanel50Layout.setVerticalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jButton4.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jButton4.setText("Cetak");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel49Layout = new javax.swing.GroupLayout(jPanel49);
        jPanel49.setLayout(jPanel49Layout);
        jPanel49Layout.setHorizontalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel49Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addComponent(jPanel50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(tgl3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel49Layout.setVerticalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel49Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel49Layout.createSequentialGroup()
                        .addComponent(tgl3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(3, 3, 3)
                        .addComponent(jPanel50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel51.setBackground(new java.awt.Color(255, 255, 255));
        jPanel51.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)), " Bulanan ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 1, 12), new java.awt.Color(188, 188, 188))); // NOI18N

        jPanel52.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel52Layout = new javax.swing.GroupLayout(jPanel52);
        jPanel52.setLayout(jPanel52Layout);
        jPanel52Layout.setHorizontalGroup(
            jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );
        jPanel52Layout.setVerticalGroup(
            jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jButton5.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jButton5.setText("Cetak");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        cbBulan1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        cbBulan1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember" }));
        cbBulan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBulan1ActionPerformed(evt);
            }
        });

        tahun1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tahun1.setForeground(new java.awt.Color(25, 181, 254));
        tahun1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tahun1.setText("tahun");

        javax.swing.GroupLayout jPanel51Layout = new javax.swing.GroupLayout(jPanel51);
        jPanel51.setLayout(jPanel51Layout);
        jPanel51Layout.setHorizontalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel51Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel51Layout.createSequentialGroup()
                        .addComponent(cbBulan1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tahun1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel51Layout.setVerticalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel51Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel51Layout.createSequentialGroup()
                        .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbBulan1)
                            .addComponent(tahun1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel51Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel53.setBackground(new java.awt.Color(255, 255, 255));
        jPanel53.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)), " Tahunan ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 1, 12), new java.awt.Color(188, 188, 188))); // NOI18N

        jPanel54.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel54Layout = new javax.swing.GroupLayout(jPanel54);
        jPanel54.setLayout(jPanel54Layout);
        jPanel54Layout.setHorizontalGroup(
            jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );
        jPanel54Layout.setVerticalGroup(
            jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jButton6.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jButton6.setText("Cetak");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        tftahun1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tftahun1.setForeground(new java.awt.Color(25, 181, 254));
        tftahun1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tftahun1.setBorder(null);

        javax.swing.GroupLayout jPanel53Layout = new javax.swing.GroupLayout(jPanel53);
        jPanel53.setLayout(jPanel53Layout);
        jPanel53Layout.setHorizontalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel53Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tftahun1))
                .addGap(18, 18, 18)
                .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel53Layout.setVerticalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel53Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel53Layout.createSequentialGroup()
                        .addComponent(tftahun1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel53Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel47Layout = new javax.swing.GroupLayout(jPanel47);
        jPanel47.setLayout(jPanel47Layout);
        jPanel47Layout.setHorizontalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel47Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel47Layout.setVerticalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel47Layout.createSequentialGroup()
                .addComponent(jPanel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 21, Short.MAX_VALUE))
        );

        jPanel46.add(jPanel47, "card2");

        jPanel55.setLayout(new java.awt.CardLayout());

        jPanel56.setBackground(new java.awt.Color(255, 255, 255));

        jPanel57.setBackground(new java.awt.Color(235, 248, 252));

        jLabel15.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(25, 181, 254));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Laporan Stok Barang");

        javax.swing.GroupLayout jPanel57Layout = new javax.swing.GroupLayout(jPanel57);
        jPanel57.setLayout(jPanel57Layout);
        jPanel57Layout.setHorizontalGroup(
            jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        jPanel57Layout.setVerticalGroup(
            jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );

        jButton7.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jButton7.setForeground(new java.awt.Color(102, 102, 102));
        jButton7.setText("Cetak");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel56Layout = new javax.swing.GroupLayout(jPanel56);
        jPanel56.setLayout(jPanel56Layout);
        jPanel56Layout.setHorizontalGroup(
            jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel56Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel56Layout.setVerticalGroup(
            jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel56Layout.createSequentialGroup()
                .addComponent(jPanel57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 90, Short.MAX_VALUE))
        );

        jPanel55.add(jPanel56, "card2");

        jPanel58.setLayout(new java.awt.CardLayout());

        jPanel59.setBackground(new java.awt.Color(255, 255, 255));

        jPanel60.setBackground(new java.awt.Color(235, 248, 252));

        jLabel18.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(25, 181, 254));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Laporan Barang Terlaris");

        javax.swing.GroupLayout jPanel60Layout = new javax.swing.GroupLayout(jPanel60);
        jPanel60.setLayout(jPanel60Layout);
        jPanel60Layout.setHorizontalGroup(
            jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
        );
        jPanel60Layout.setVerticalGroup(
            jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );

        jPanel61.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel61Layout = new javax.swing.GroupLayout(jPanel61);
        jPanel61.setLayout(jPanel61Layout);
        jPanel61Layout.setHorizontalGroup(
            jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 153, Short.MAX_VALUE)
        );
        jPanel61Layout.setVerticalGroup(
            jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel62.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel62Layout = new javax.swing.GroupLayout(jPanel62);
        jPanel62.setLayout(jPanel62Layout);
        jPanel62Layout.setHorizontalGroup(
            jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 153, Short.MAX_VALUE)
        );
        jPanel62Layout.setVerticalGroup(
            jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jLabel20.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("s / d");

        jButton8.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jButton8.setText("Cetak");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel59Layout = new javax.swing.GroupLayout(jPanel59);
        jPanel59.setLayout(jPanel59Layout);
        jPanel59Layout.setHorizontalGroup(
            jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel60, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel59Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel59Layout.createSequentialGroup()
                        .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(xtgl1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(xtgl2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel59Layout.setVerticalGroup(
            jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel59Layout.createSequentialGroup()
                .addComponent(jPanel60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel59Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel59Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(xtgl1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(xtgl2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        jPanel58.add(jPanel59, "card2");

        javax.swing.GroupLayout lapLayout = new javax.swing.GroupLayout(lap);
        lap.setLayout(lapLayout);
        lapLayout.setHorizontalGroup(
            lapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lapLayout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addGroup(lapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(62, 62, 62)
                .addGroup(lapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(95, Short.MAX_VALUE))
        );
        lapLayout.setVerticalGroup(
            lapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lapLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(lapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        mainpane.add(lap, "card13");

        mngUser.setBackground(new java.awt.Color(248, 248, 248));

        jPanel63.setBackground(new java.awt.Color(235, 248, 252));

        jLabel22.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(25, 181, 254));
        jLabel22.setText("Manage User");

        javax.swing.GroupLayout jPanel63Layout = new javax.swing.GroupLayout(jPanel63);
        jPanel63.setLayout(jPanel63Layout);
        jPanel63Layout.setHorizontalGroup(
            jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel63Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 1040, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel63Layout.setVerticalGroup(
            jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
        );

        jPanel64.setLayout(new java.awt.CardLayout());

        jPanel65.setBackground(new java.awt.Color(255, 255, 255));

        btnTambah6.setBackground(new java.awt.Color(25, 181, 254));
        btnTambah6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTambah6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnTambah6MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnTambah6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnTambah6MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnTambah6MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnTambah6MouseReleased(evt);
            }
        });

        jLabel59.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(255, 255, 255));
        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add.png"))); // NOI18N
        jLabel59.setText("Tambah User");
        jLabel59.setIconTextGap(12);

        javax.swing.GroupLayout btnTambah6Layout = new javax.swing.GroupLayout(btnTambah6);
        btnTambah6.setLayout(btnTambah6Layout);
        btnTambah6Layout.setHorizontalGroup(
            btnTambah6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel59, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
        );
        btnTambah6Layout.setVerticalGroup(
            btnTambah6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel59, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
        );

        jPanel67.setBackground(new java.awt.Color(235, 248, 252));

        jLabel63.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel63.setForeground(new java.awt.Color(25, 181, 254));
        jLabel63.setText("Jumlah User :");

        jmlUsr.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jmlUsr.setForeground(new java.awt.Color(25, 181, 254));
        jmlUsr.setText("x");

        javax.swing.GroupLayout jPanel67Layout = new javax.swing.GroupLayout(jPanel67);
        jPanel67.setLayout(jPanel67Layout);
        jPanel67Layout.setHorizontalGroup(
            jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel67Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jmlUsr, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(530, Short.MAX_VALUE))
        );
        jPanel67Layout.setVerticalGroup(
            jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel63, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
            .addComponent(jmlUsr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnUbah6.setBackground(new java.awt.Color(33, 191, 115));
        btnUbah6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUbah6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUbah6MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnUbah6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnUbah6MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnUbah6MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnUbah6MouseReleased(evt);
            }
        });

        jLabel60.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(255, 255, 255));
        jLabel60.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel60.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ubah.png"))); // NOI18N
        jLabel60.setText("Ubah");
        jLabel60.setIconTextGap(12);

        javax.swing.GroupLayout btnUbah6Layout = new javax.swing.GroupLayout(btnUbah6);
        btnUbah6.setLayout(btnUbah6Layout);
        btnUbah6Layout.setHorizontalGroup(
            btnUbah6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel60, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
        );
        btnUbah6Layout.setVerticalGroup(
            btnUbah6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel60, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
        );

        btnHapus6.setBackground(new java.awt.Color(255, 46, 49));
        btnHapus6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHapus6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnHapus6MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnHapus6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnHapus6MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnHapus6MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnHapus6MouseReleased(evt);
            }
        });

        jLabel61.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(255, 255, 255));
        jLabel61.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/hapus.png"))); // NOI18N
        jLabel61.setText("Hapus");
        jLabel61.setIconTextGap(12);

        javax.swing.GroupLayout btnHapus6Layout = new javax.swing.GroupLayout(btnHapus6);
        btnHapus6.setLayout(btnHapus6Layout);
        btnHapus6Layout.setHorizontalGroup(
            btnHapus6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel61, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
        );
        btnHapus6Layout.setVerticalGroup(
            btnHapus6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel61, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
        );

        jScrollPane7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        tUsers.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        tUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tUsers.setGridColor(new java.awt.Color(255, 255, 255));
        tUsers.setRowHeight(25);
        tUsers.setSelectionBackground(new java.awt.Color(25, 181, 254));
        tUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tUsersMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(tUsers);

        javax.swing.GroupLayout jPanel70Layout = new javax.swing.GroupLayout(jPanel70);
        jPanel70.setLayout(jPanel70Layout);
        jPanel70Layout.setHorizontalGroup(
            jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
        );
        jPanel70Layout.setVerticalGroup(
            jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
        );

        jPanel71.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel71Layout = new javax.swing.GroupLayout(jPanel71);
        jPanel71.setLayout(jPanel71Layout);
        jPanel71Layout.setHorizontalGroup(
            jPanel71Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );
        jPanel71Layout.setVerticalGroup(
            jPanel71Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel72.setBackground(new java.awt.Color(235, 248, 252));

        lbAksi.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        lbAksi.setForeground(new java.awt.Color(25, 181, 254));
        lbAksi.setText("Tambah Data");

        javax.swing.GroupLayout jPanel72Layout = new javax.swing.GroupLayout(jPanel72);
        jPanel72.setLayout(jPanel72Layout);
        jPanel72Layout.setHorizontalGroup(
            jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel72Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbAksi, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE))
        );
        jPanel72Layout.setVerticalGroup(
            jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbAksi, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
        );

        jLabel65.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel65.setForeground(new java.awt.Color(188, 188, 188));
        jLabel65.setText("User For");

        cbKdUsr.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        cbKdUsr.setForeground(new java.awt.Color(25, 181, 254));
        cbKdUsr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbKdUsrActionPerformed(evt);
            }
        });

        jPanel73.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel73Layout = new javax.swing.GroupLayout(jPanel73);
        jPanel73.setLayout(jPanel73Layout);
        jPanel73Layout.setHorizontalGroup(
            jPanel73Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 319, Short.MAX_VALUE)
        );
        jPanel73Layout.setVerticalGroup(
            jPanel73Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        ulnama.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout ulnamaLayout = new javax.swing.GroupLayout(ulnama);
        ulnama.setLayout(ulnamaLayout);
        ulnamaLayout.setHorizontalGroup(
            ulnamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 319, Short.MAX_VALUE)
        );
        ulnamaLayout.setVerticalGroup(
            ulnamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jLabel66.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(188, 188, 188));
        jLabel66.setText("Nama");

        tfnama.setEditable(false);
        tfnama.setBackground(new java.awt.Color(248, 248, 248));
        tfnama.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfnama.setForeground(new java.awt.Color(25, 181, 254));
        tfnama.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfnama.setBorder(null);

        uluname.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout ulunameLayout = new javax.swing.GroupLayout(uluname);
        uluname.setLayout(ulunameLayout);
        ulunameLayout.setHorizontalGroup(
            ulunameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 319, Short.MAX_VALUE)
        );
        ulunameLayout.setVerticalGroup(
            ulunameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jLabel67.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel67.setForeground(new java.awt.Color(188, 188, 188));
        jLabel67.setText("Username");

        tfuname.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfuname.setBorder(null);
        tfuname.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfunameKeyReleased(evt);
            }
        });

        ulpass.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout ulpassLayout = new javax.swing.GroupLayout(ulpass);
        ulpass.setLayout(ulpassLayout);
        ulpassLayout.setHorizontalGroup(
            ulpassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 319, Short.MAX_VALUE)
        );
        ulpassLayout.setVerticalGroup(
            ulpassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jLabel68.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(188, 188, 188));
        jLabel68.setText("Password");

        tfpass.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfpass.setBorder(null);
        tfpass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfpassKeyReleased(evt);
            }
        });

        jPanel77.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel77Layout = new javax.swing.GroupLayout(jPanel77);
        jPanel77.setLayout(jPanel77Layout);
        jPanel77Layout.setHorizontalGroup(
            jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 319, Short.MAX_VALUE)
        );
        jPanel77Layout.setVerticalGroup(
            jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jLabel69.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel69.setForeground(new java.awt.Color(188, 188, 188));
        jLabel69.setText("Jabatan / Level");

        btnSimpan6.setBackground(new java.awt.Color(25, 181, 254));
        btnSimpan6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSimpan6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSimpan6MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSimpan6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSimpan6MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnSimpan6MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnSimpan6MouseReleased(evt);
            }
        });

        jLabel73.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel73.setForeground(new java.awt.Color(255, 255, 255));
        jLabel73.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel73.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/simpan.png"))); // NOI18N
        jLabel73.setText("Simpan");
        jLabel73.setIconTextGap(12);

        javax.swing.GroupLayout btnSimpan6Layout = new javax.swing.GroupLayout(btnSimpan6);
        btnSimpan6.setLayout(btnSimpan6Layout);
        btnSimpan6Layout.setHorizontalGroup(
            btnSimpan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel73, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
        );
        btnSimpan6Layout.setVerticalGroup(
            btnSimpan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel73, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
        );

        jLabel70.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel70.setForeground(new java.awt.Color(188, 188, 188));
        jLabel70.setText("Foto");

        uImg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        uImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/avatar.png"))); // NOI18N
        uImg.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        btnBatal.setBackground(new java.awt.Color(255, 255, 255));
        btnBatal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(188, 188, 188)));
        btnBatal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBatal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBatalMouseClicked(evt);
            }
        });

        jLabel72.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(102, 102, 102));
        jLabel72.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel72.setText("Cancel");

        javax.swing.GroupLayout btnBatalLayout = new javax.swing.GroupLayout(btnBatal);
        btnBatal.setLayout(btnBatalLayout);
        btnBatalLayout.setHorizontalGroup(
            btnBatalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel72, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btnBatalLayout.setVerticalGroup(
            btnBatalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel72, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
        );

        iuname.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iuname.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N

        ipass.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ipass.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N

        tfLevel.setEditable(false);
        tfLevel.setBackground(new java.awt.Color(248, 248, 248));
        tfLevel.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfLevel.setForeground(new java.awt.Color(25, 181, 254));
        tfLevel.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfLevel.setBorder(null);

        jPanel66.setBackground(new java.awt.Color(218, 218, 218));
        jPanel66.setPreferredSize(new java.awt.Dimension(319, 1));

        javax.swing.GroupLayout jPanel66Layout = new javax.swing.GroupLayout(jPanel66);
        jPanel66.setLayout(jPanel66Layout);
        jPanel66Layout.setHorizontalGroup(
            jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 319, Short.MAX_VALUE)
        );
        jPanel66Layout.setVerticalGroup(
            jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel65Layout = new javax.swing.GroupLayout(jPanel65);
        jPanel65.setLayout(jPanel65Layout);
        jPanel65Layout.setHorizontalGroup(
            jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel65Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addComponent(btnTambah6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnUbah6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnHapus6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel73, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ulnama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uluname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ulpass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel77, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel65Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbKdUsr, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tfnama, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel65Layout.createSequentialGroup()
                                        .addComponent(tfuname, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(iuname, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel65Layout.createSequentialGroup()
                                        .addComponent(tfpass, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(ipass, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel65Layout.createSequentialGroup()
                                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtkode_temp, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(52, 52, 52)
                                .addComponent(uImg, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(tfLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnSimpan6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel65Layout.setVerticalGroup(
            jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel65Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnTambah6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUbah6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnHapus6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(11, 11, 11)
                        .addComponent(jPanel70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jPanel67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addComponent(jPanel72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jLabel65)
                        .addGap(6, 6, 6)
                        .addComponent(cbKdUsr, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jPanel73, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel66)
                        .addGap(6, 6, 6)
                        .addComponent(tfnama, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(ulnama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel67)
                        .addGap(6, 6, 6)
                        .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfuname, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(iuname, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addComponent(uluname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel68)
                        .addGap(6, 6, 6)
                        .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfpass, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ipass, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addComponent(ulpass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel69)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel77, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel65Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel70)
                                .addGap(6, 6, 6)
                                .addComponent(txtkode_temp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel65Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(uImg, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSimpan6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
            .addComponent(jPanel71, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel64.add(jPanel65, "card2");

        javax.swing.GroupLayout mngUserLayout = new javax.swing.GroupLayout(mngUser);
        mngUser.setLayout(mngUserLayout);
        mngUserLayout.setHorizontalGroup(
            mngUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel63, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mngUserLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel64, javax.swing.GroupLayout.PREFERRED_SIZE, 1040, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mngUserLayout.setVerticalGroup(
            mngUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mngUserLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel64, javax.swing.GroupLayout.PREFERRED_SIZE, 594, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainpane.add(mngUser, "card14");

        main.add(mainpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 26, 1060, 672));

        bgmain.add(main, new java.awt.GridBagConstraints());

        javax.swing.GroupLayout bgLayout = new javax.swing.GroupLayout(bg);
        bg.setLayout(bgLayout);
        bgLayout.setHorizontalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bgLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bgmain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        bgLayout.setVerticalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bgLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(bgmain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(bg, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
fadeIn(this);        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowOpened

    private void exitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMouseClicked
fadeOut(this);
System.exit(0);        // TODO add your handling code here:
    }//GEN-LAST:event_exitMouseClicked

    private void minMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minMouseClicked
this.setState(Dashboard.ICONIFIED);       // TODO add your handling code here:
    }//GEN-LAST:event_minMouseClicked

    private void bDashMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bDashMousePressed
        dashActive();
    }//GEN-LAST:event_bDashMousePressed

    private void bMasterMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bMasterMousePressed
        bMasterPressed();// TODO add your handling code here:
    }//GEN-LAST:event_bMasterMousePressed

    private void bTransMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bTransMousePressed
        setColor(bTrans,act3,ltrans);
        resetColor(bDash,act1,ldash);
        resetColor(bMaster,act2,lmaster);
        resetColor(bLap,act4,llap);
        resetColor(bMUser,act5,lmuser);
        resetColor(bKeluar,act6,lkeluar);
        setIcon(ltrans, "/img/trans_b.png");
        setIcon(ldash, "/img/home_g.png");
        setIcon(lmaster, "/img/master_g.png");
        setIcon(llap, "/img/report_g.png");
        setIcon(lmuser, "/img/mnguser_g.png");
        setIcon(lkeluar, "/img/exit_g.png");
        trans.setVisible(true);
        setPanelVisible(dashGudang, dashKasir, master, master_barang, master_pelanggan, master_supplier, false);
        setPanelVisible(master_merk, master_satuan, master_pegawai, dashAdmin, lap, mngUser, false);// TODO add your handling code here:
    }//GEN-LAST:event_bTransMousePressed

    private void bLapMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bLapMousePressed
        setColor(bLap,act4,llap);
        resetColor(bDash,act1,ldash);
        resetColor(bMaster,act2,lmaster);
        resetColor(bTrans,act3,ltrans);
        resetColor(bMUser,act5,lmuser);
        resetColor(bKeluar,act6,lkeluar);
        setIcon(llap, "/img/report_b.png");
        setIcon(ldash, "/img/home_g.png");
        setIcon(lmaster, "/img/master_g.png");
        setIcon(ltrans, "/img/trans_g.png");
        setIcon(lmuser, "/img/mnguser_g.png");
        setIcon(lkeluar, "/img/exit_g.png");
        lap.setVisible(true);
        setPanelVisible(dashGudang, dashKasir, master, master_barang, master_pelanggan, master_supplier, false);
        setPanelVisible(master_merk, master_satuan, master_pegawai, trans, dashAdmin, mngUser, false);// TODO add your handling code here:
    }//GEN-LAST:event_bLapMousePressed

    private void bMUserMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bMUserMousePressed
if ("Admin".equals(userLevel)) {
        setColor(bMUser,act5,lmuser);
        resetColor(bDash,act1,ldash);
        resetColor(bMaster,act2,lmaster);
        resetColor(bTrans,act3,ltrans);
        resetColor(bLap,act4,llap);
        resetColor(bKeluar,act6,lkeluar);
        setIcon(lmuser, "/img/mnguser_b.png");
        setIcon(llap, "/img/report_g.png");
        setIcon(ldash, "/img/home_g.png");
        setIcon(lmaster, "/img/master_g.png");
        setIcon(ltrans, "/img/trans_g.png");
        setIcon(lkeluar, "/img/exit_g.png");
        getKdUsr();
        kosongUser();
        dataUsers();
        mngUser.setVisible(true);
        setPanelVisible(dashGudang, dashKasir, master, master_barang, master_pelanggan, master_supplier, false);
        setPanelVisible(master_merk, master_satuan, master_pegawai, trans, lap, dashAdmin, false);    
} else JOptionPane.showMessageDialog(null, "Access Denied !");// TODO add your handling code here:
    }//GEN-LAST:event_bMUserMousePressed

    private void bKeluarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bKeluarMousePressed
        setColor(bKeluar,act6,lkeluar);
        resetColor(bMUser,act5,lmuser);
        resetColor(bDash,act1,ldash);
        resetColor(bMaster,act2,lmaster);
        resetColor(bTrans,act3,ltrans);
        resetColor(bLap,act4,llap);
        setIcon(lkeluar, "/img/exit_b.png");
        setIcon(lmuser, "/img/mnguser_g.png");
        setIcon(llap, "/img/report_g.png");
        setIcon(ldash, "/img/home_g.png");
        setIcon(lmaster, "/img/master_g.png");
        setIcon(ltrans, "/img/trans_g.png");
        int ok = JOptionPane.showConfirmDialog(null, "Apakah anda yakin ingin keluar?\n\n", "Konfirmasi", JOptionPane.OK_CANCEL_OPTION);
        if(ok==0) {
            fadeOut(this);
            this.dispose();
            new Login().setVisible(true);
        }// TODO add your handling code here:
    }//GEN-LAST:event_bKeluarMousePressed

    private void pegawaiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pegawaiMouseClicked
if("Admin".equals(userLevel)) {
    master_pegawai.setVisible(true);
    setPanelVisible(dashGudang, dashKasir, dashAdmin, master_barang, master_pelanggan, master_supplier, false);
    setPanelVisible(master_merk, master_satuan, master, trans, lap, mngUser, false);
    dataPegawai();
} else JOptionPane.showMessageDialog(null, "Access Denied !");// TODO add your handling code here:
    }//GEN-LAST:event_pegawaiMouseClicked

    private void pegawaiMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pegawaiMouseEntered
setItemColor(bg3, new Color(228, 248, 243)); 
setIconMenu(iconPegawai,"/img/pegawai.png",lb_pegawai, new Color(25, 181, 254));// TODO add your handling code here:
    }//GEN-LAST:event_pegawaiMouseEntered

    private void pegawaiMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pegawaiMouseExited
setItemColor(bg3, new Color(239, 239, 239));
setIconMenu(iconPegawai,"/img/pegawai_g.png",lb_pegawai, new Color(188, 188, 188));// TODO add your handling code here:
    }//GEN-LAST:event_pegawaiMouseExited

    private void pegawaiMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pegawaiMousePressed
setItemColor(bg3, new Color(6, 166, 224));         // TODO add your handling code here:
    }//GEN-LAST:event_pegawaiMousePressed

    private void pegawaiMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pegawaiMouseReleased
setItemColor(bg3, new Color(228, 248, 243));        // TODO add your handling code here:
    }//GEN-LAST:event_pegawaiMouseReleased

    private void merkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_merkMouseClicked
if("Admin".equals(userLevel) || "Staf Gudang".equals(userLevel)) {
    master_merk.setVisible(true);
    setPanelVisible(dashGudang, dashKasir, dashAdmin, master_barang, master_pelanggan, master_pegawai, false);
    setPanelVisible(master_satuan, master_supplier, master, trans, lap, mngUser, false);
    dataMerk();  
} else JOptionPane.showMessageDialog(null, "Access Denied !");// TODO add your handling code here:
    }//GEN-LAST:event_merkMouseClicked

    private void merkMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_merkMouseEntered
setItemColor(bg4, new Color(228, 248, 243)); 
setIconMenu(iconMerk,"/img/merk.png",lb_merk, new Color(25, 181, 254));// TODO add your handling code here:
    }//GEN-LAST:event_merkMouseEntered

    private void merkMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_merkMouseExited
setItemColor(bg4, new Color(239, 239, 239));
setIconMenu(iconMerk,"/img/merk_g.png",lb_merk, new Color(188, 188, 188));// TODO add your handling code here:
    }//GEN-LAST:event_merkMouseExited

    private void merkMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_merkMousePressed
setItemColor(bg4, new Color(6, 166, 224));        // TODO add your handling code here:
    }//GEN-LAST:event_merkMousePressed

    private void merkMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_merkMouseReleased
setItemColor(bg4, new Color(228, 248, 243));        // TODO add your handling code here:
    }//GEN-LAST:event_merkMouseReleased

    private void satuanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_satuanMouseClicked
if("Admin".equals(userLevel) || "Staf Gudang".equals(userLevel)) {
    master_satuan.setVisible(true);
    setPanelVisible(dashGudang, dashKasir, dashAdmin, master_barang, master_pelanggan, master_pegawai, false);
    setPanelVisible(master_merk, master_supplier, master, trans, lap, mngUser, false);
    dataSatuan();  
} else JOptionPane.showMessageDialog(null, "Access Denied !");// TODO add your handling code here:
    }//GEN-LAST:event_satuanMouseClicked

    private void satuanMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_satuanMouseEntered
setItemColor(bg5, new Color(228, 248, 243));
setIconMenu(iconSatuan,"/img/satuan.png",lb_satuan, new Color(25, 181, 254));// TODO add your handling code here:
    }//GEN-LAST:event_satuanMouseEntered

    private void satuanMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_satuanMouseExited
setItemColor(bg5, new Color(239, 239, 239));
setIconMenu(iconSatuan,"/img/satuan_g.png",lb_satuan, new Color(188, 188, 188));// TODO add your handling code here:
    }//GEN-LAST:event_satuanMouseExited

    private void satuanMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_satuanMousePressed
setItemColor(bg5, new Color(6, 166, 224));        // TODO add your handling code here:
    }//GEN-LAST:event_satuanMousePressed

    private void satuanMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_satuanMouseReleased
setItemColor(bg5, new Color(228, 248, 243));        // TODO add your handling code here:
    }//GEN-LAST:event_satuanMouseReleased

    private void supplierMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_supplierMouseClicked
if("Admin".equals(userLevel) || "Staf Gudang".equals(userLevel)) {
    master_supplier.setVisible(true);
    setPanelVisible(dashGudang, dashKasir, dashAdmin, master_barang, master_pelanggan, master_pegawai, false);
    setPanelVisible(master_merk, master_satuan, master, trans, lap, mngUser, false);
    dataSupplier();     
}else JOptionPane.showMessageDialog(null, "Access Denied !");// TODO add your handling code here:
    }//GEN-LAST:event_supplierMouseClicked

    private void supplierMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_supplierMouseEntered
setItemColor(bg6, new Color(228, 248, 243)); 
setIconMenu(iconSupplier,"/img/supplier.png",lb_supplier, new Color(25, 181, 254));// TODO add your handling code here:
    }//GEN-LAST:event_supplierMouseEntered

    private void supplierMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_supplierMouseExited
setItemColor(bg6, new Color(239, 239, 239));
setIconMenu(iconSupplier,"/img/supplier_g.png",lb_supplier, new Color(188, 188, 188));// TODO add your handling code here:
    }//GEN-LAST:event_supplierMouseExited

    private void supplierMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_supplierMousePressed
setItemColor(bg6, new Color(6, 166, 224));         // TODO add your handling code here:
    }//GEN-LAST:event_supplierMousePressed

    private void supplierMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_supplierMouseReleased
setItemColor(bg6, new Color(228, 248, 243));         // TODO add your handling code here:
    }//GEN-LAST:event_supplierMouseReleased

    private void jualMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jualMouseClicked
if("Admin".equals(userLevel) || "Staf Kasir".equals(userLevel)) {
    new TrxPenjualan(userName).setVisible(true);
} else JOptionPane.showMessageDialog(null, "Access Denied !");        // TODO add your handling code here:
    }//GEN-LAST:event_jualMouseClicked

    private void jualMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jualMouseEntered
setItemColor(bgjual, new Color(228, 248, 243));
setIconMenu(iconSell,"/img/sell.png",lb_sell, new Color(25, 181, 254));// TODO add your handling code here:
    }//GEN-LAST:event_jualMouseEntered

    private void jualMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jualMouseExited
setItemColor(bgjual, new Color(239, 239, 239));
setIconMenu(iconSell,"/img/sell_g.png",lb_sell, new Color(188, 188, 188));// TODO add your handling code here:
    }//GEN-LAST:event_jualMouseExited

    private void jualMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jualMousePressed
setItemColor(bgjual, new Color(6, 166, 224));        // TODO add your handling code here:
    }//GEN-LAST:event_jualMousePressed

    private void jualMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jualMouseReleased
setItemColor(bgjual, new Color(228, 248, 243));        // TODO add your handling code here:
    }//GEN-LAST:event_jualMouseReleased

    private void beliMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_beliMouseClicked
if("Admin".equals(userLevel) || "Staf Gudang".equals(userLevel)) {
    new TrxPembelian(userName).setVisible(true);  
} else JOptionPane.showMessageDialog(null, " Access Denied !");// TODO add your handling code here:
    }//GEN-LAST:event_beliMouseClicked

    private void beliMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_beliMouseEntered
setItemColor(bgbeli, new Color(228, 248, 243));
setIconMenu(iconBuy,"/img/buy.png",lb_buy, new Color(25, 181, 254));// TODO add your handling code here:
    }//GEN-LAST:event_beliMouseEntered

    private void beliMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_beliMouseExited
setItemColor(bgbeli, new Color(239, 239, 239));
setIconMenu(iconBuy,"/img/buy_g.png",lb_buy, new Color(188, 188, 188));// TODO add your handling code here:
    }//GEN-LAST:event_beliMouseExited

    private void beliMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_beliMousePressed
setItemColor(bgbeli, new Color(6, 166, 224));         // TODO add your handling code here:
    }//GEN-LAST:event_beliMousePressed

    private void beliMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_beliMouseReleased
setItemColor(bgbeli, new Color(228, 248, 243));         // TODO add your handling code here:
    }//GEN-LAST:event_beliMouseReleased

    private void barangMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barangMouseReleased
        setItemColor(bg1, new Color(228, 248, 243));        // TODO add your handling code here:
    }//GEN-LAST:event_barangMouseReleased

    private void barangMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barangMousePressed
        setItemColor(bg1, new Color(6, 166, 224));// TODO add your handling code here:
    }//GEN-LAST:event_barangMousePressed

    private void barangMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barangMouseExited
        setItemColor(bg1, new Color(239, 239, 239));
        setIconMenu(iconBarang,"/img/barang_g.png",lb_barang, new Color(188, 188, 188));// TODO add your handling code here:
    }//GEN-LAST:event_barangMouseExited

    private void barangMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barangMouseEntered
        setItemColor(bg1, new Color(228, 248, 243));
        setIconMenu(iconBarang,"/img/barang.png",lb_barang, new Color(25, 181, 254));// TODO add your handling code here:
    }//GEN-LAST:event_barangMouseEntered

    private void barangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barangMouseClicked
if("Admin".equals(userLevel) || "Staf Gudang".equals(userLevel)) {
    master_barang.setVisible(true);
    setPanelVisible(dashGudang, dashKasir, dashAdmin, master_pegawai, master_pelanggan, master_supplier, false);
    setPanelVisible(master_merk, master_satuan, master, trans, lap, mngUser, false);
    dataBarang();     
} else JOptionPane.showMessageDialog(null, "Access Denied !");
        
       // TODO add your handling code here:
    }//GEN-LAST:event_barangMouseClicked

    private void pelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pelangganMouseClicked
if("Admin".equals(userLevel) || "Staf Kasir".equals(userLevel)) {
    master_pelanggan.setVisible(true);
    setPanelVisible(dashGudang, dashKasir, dashAdmin, master_barang, master_pegawai, master_supplier, false);
    setPanelVisible(master_merk, master_satuan, master, trans, lap, mngUser, false);
    dataPelanggan();  
}else JOptionPane.showMessageDialog(null, "Access Denied");// TODO add your handling code here:
    }//GEN-LAST:event_pelangganMouseClicked

    private void pelangganMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pelangganMouseEntered
       setItemColor(bg2, new Color(228, 248, 243));
       setIconMenu(iconPelanggan,"/img/pelanggan.png",lb_pelanggan, new Color(25, 181, 254));// TODO add your handling code here:
    }//GEN-LAST:event_pelangganMouseEntered

    private void pelangganMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pelangganMouseExited
       setItemColor(bg2, new Color(239, 239, 239));
        setIconMenu(iconPelanggan,"/img/pelanggan_g.png",lb_pelanggan, new Color(188, 188, 188));// TODO add your handling code here:
    }//GEN-LAST:event_pelangganMouseExited

    private void pelangganMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pelangganMousePressed
       setItemColor(bg2, new Color(6, 166, 224));// TODO add your handling code here:
    }//GEN-LAST:event_pelangganMousePressed

    private void pelangganMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pelangganMouseReleased
       setItemColor(bg2, new Color(228, 248, 243));  // TODO add your handling code here:
    }//GEN-LAST:event_pelangganMouseReleased

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
bMasterPressed();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel10MouseClicked

    private void btnTambahMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambahMouseClicked
FrmAnEPegawai fr = new FrmAnEPegawai();  
fr.setField(true, "");
fr.setVisible(true);// TODO add your handling code here:
    }//GEN-LAST:event_btnTambahMouseClicked

    private void btnTambahMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambahMouseEntered
setItemColor(btnTambah, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahMouseEntered

    private void btnTambahMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambahMouseExited
setItemColor(btnTambah, new Color(25, 181, 254));         // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahMouseExited

    private void btnTambahMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambahMousePressed
setItemColor(btnTambah, new Color(6, 166, 224));         // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahMousePressed

    private void btnTambahMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambahMouseReleased
setItemColor(btnTambah, new Color(81, 204, 255));         // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahMouseReleased

    private void btnUbahMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbahMouseClicked
try{
    int countRows[] = tbl_pgw.getSelectedRows();
    if(countRows.length != 0){        
        int row = tbl_pgw.getSelectedRow();
        String code = tbl_pgw.getValueAt(row, 1).toString();
        FrmAnEPegawai fr = new FrmAnEPegawai();  
        fr.setField(false, code);
        fr.setVisible(true);
    }else JOptionPane.showMessageDialog(null, "Pilih dulu datanya atuh mang!");    
}catch (HeadlessException e){
    JOptionPane.showMessageDialog(rootPane, e);
}// TODO add your handling code here:
    }//GEN-LAST:event_btnUbahMouseClicked

    private void btnUbahMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbahMouseEntered
setItemColor(btnUbah, new Color(89,214,116));         // TODO add your handling code here:
    }//GEN-LAST:event_btnUbahMouseEntered

    private void btnUbahMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbahMouseExited
setItemColor(btnUbah, new Color(33,191,115));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbahMouseExited

    private void btnUbahMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbahMousePressed
setItemColor(btnUbah, new Color(14,176,85));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbahMousePressed

    private void btnUbahMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbahMouseReleased
setItemColor(btnUbah, new Color(89,214,116));         // TODO add your handling code here:
    }//GEN-LAST:event_btnUbahMouseReleased

    private void btnHapusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseClicked
try{
    int countRows[] = tbl_pgw.getSelectedRows();
    if(countRows.length != 0){
        String kode;
        String nama;
        StringBuilder sb = new StringBuilder();
        for (int a=0;a<countRows.length;a++){
            int rows = countRows[a];
            kode = tbl_pgw.getValueAt(rows, 1).toString();
            nama = tbl_pgw.getValueAt(rows, 2).toString();
            sb.append(a+1).append(". ").append(kode).append(" ").append(nama).append("\n");       
        }
        int ok = JOptionPane.showConfirmDialog(null, "Anda yakin ingin menghapus data ini?\n\n"+sb+"\n", "Konfirmasi", JOptionPane.OK_CANCEL_OPTION);
        if(ok==0) {
            try {            
                String sql;                
                for (int i=0;i<countRows.length;i++){
                    int row = countRows[i];
                    String code = tbl_pgw.getValueAt(row, 1).toString();
                    sql = "delete from pegawai where kode_pegawai = '"+code+"'";
                    Statement rs = conn.createStatement();
                    rs.executeUpdate(sql);
                }
                Alert al = new Alert("Deleted Successfully", "/img/ok.png", new Color(33,191,115));
                al.setVisible(true);
                al.fadeOut(al);
                al.setVisible(false);
                dataPegawai();
                tfcari.requestFocus();
            }catch (HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }else JOptionPane.showMessageDialog(null, "Pilih dulu datanya atuh mang!");    
}catch (HeadlessException e){
    JOptionPane.showMessageDialog(rootPane, e);
}        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusMouseClicked

    private void btnHapusMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseEntered
setItemColor(btnHapus, new Color(255,115,99));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusMouseEntered

    private void btnHapusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseExited
setItemColor(btnHapus, new Color(255,46,49));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusMouseExited

    private void btnHapusMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMousePressed
setItemColor(btnHapus, new Color(236,31,19));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusMousePressed

    private void btnHapusMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseReleased
setItemColor(btnHapus, new Color(255,155,99));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusMouseReleased

    private void tfcariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfcariKeyReleased
try{
    if (tfcari.getText().length()!=0){
        dataPegawai();
        sum_cari.setText(String.valueOf(tbl_pgw.getRowCount()));
    }else {
        dataPegawai();
        sum_cari.setText("0");
    }     
}catch (Exception e){
    JOptionPane.showMessageDialog(rootPane, e);
}         // TODO add your handling code here:
    }//GEN-LAST:event_tfcariKeyReleased

    private void jLabel16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel16MouseClicked
bMasterPressed();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel16MouseClicked

    private void btnTambah1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah1MouseClicked
FrmAnEPelanggan fr = new FrmAnEPelanggan();  
fr.setField(true, "");
fr.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah1MouseClicked

    private void btnTambah1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah1MouseEntered
setItemColor(btnTambah1, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah1MouseEntered

    private void btnTambah1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah1MouseExited
setItemColor(btnTambah1, new Color(25, 181, 254));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah1MouseExited

    private void btnTambah1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah1MousePressed
setItemColor(btnTambah1, new Color(6, 166, 224));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah1MousePressed

    private void btnTambah1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah1MouseReleased
setItemColor(btnTambah1, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah1MouseReleased

    private void btnUbah1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah1MouseClicked
try{
    int countRows[] = tbl_plg.getSelectedRows();
    if(countRows.length != 0){        
        int row = tbl_plg.getSelectedRow();
        String code = tbl_plg.getValueAt(row, 1).toString();
        FrmAnEPelanggan fr = new FrmAnEPelanggan();  
        fr.setField(false, code);
        fr.setVisible(true);
    }else JOptionPane.showMessageDialog(null, "Pilih dulu datanya atuh mang!");    
}catch (HeadlessException e){
    JOptionPane.showMessageDialog(rootPane, e);
}        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah1MouseClicked

    private void btnUbah1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah1MouseEntered
setItemColor(btnUbah1, new Color(89,214,116));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah1MouseEntered

    private void btnUbah1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah1MouseExited
setItemColor(btnUbah1, new Color(33,191,115));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah1MouseExited

    private void btnUbah1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah1MousePressed
setItemColor(btnUbah1, new Color(14,176,85));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah1MousePressed

    private void btnUbah1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah1MouseReleased
setItemColor(btnUbah1, new Color(89,214,116));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah1MouseReleased

    private void btnHapus1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus1MouseClicked
try{
    int countRows[] = tbl_plg.getSelectedRows();
    if(countRows.length != 0){
        String kode;
        String nama;
        StringBuilder sb = new StringBuilder();
        for (int a=0;a<countRows.length;a++){
            int rows = countRows[a];
            kode = tbl_plg.getValueAt(rows, 1).toString();
            nama = tbl_plg.getValueAt(rows, 2).toString();
            sb.append(a+1).append(". ").append(kode).append(" ").append(nama).append("\n");       
        }
        int ok = JOptionPane.showConfirmDialog(null, "Anda yakin ingin menghapus data ini?\n\n"+sb+"\n", "Konfirmasi", JOptionPane.OK_CANCEL_OPTION);
        if(ok==0) {
            try {            
                String sql;                
                for (int i=0;i<countRows.length;i++){
                    int row = countRows[i];
                    String code = tbl_plg.getValueAt(row, 1).toString();
                    sql = "delete from pelanggan where kode_pelanggan = '"+code+"'";
                    Statement rs = conn.createStatement();
                    rs.executeUpdate(sql);
                }
                Alert al = new Alert("Deleted Successfully", "/img/ok.png", new Color(33,191,115));
                al.setVisible(true);
                al.fadeOut(al);
                al.setVisible(false);
                dataPelanggan();
                tfcari1.requestFocus();
            }catch (HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }else JOptionPane.showMessageDialog(null, "Pilih dulu datanya atuh mang!");    
}catch (HeadlessException e){
    JOptionPane.showMessageDialog(rootPane, e);        
    }         // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus1MouseClicked

    private void btnHapus1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus1MouseEntered
setItemColor(btnHapus1, new Color(255,115,99));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus1MouseEntered

    private void btnHapus1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus1MouseExited
setItemColor(btnHapus1, new Color(255,46,49));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus1MouseExited

    private void btnHapus1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus1MousePressed
setItemColor(btnHapus1, new Color(236,31,19));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus1MousePressed

    private void btnHapus1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus1MouseReleased
setItemColor(btnHapus1, new Color(255,155,99));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus1MouseReleased

    private void tfcari1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfcari1KeyReleased
try{
    if (tfcari1.getText().length()!=0){
        dataPelanggan();
        sum_cari1.setText(String.valueOf(tbl_plg.getRowCount()));
    }else {
        dataPelanggan();
        sum_cari1.setText("0");
    }     
}catch (Exception e){
    JOptionPane.showMessageDialog(rootPane, e);
}        // TODO add your handling code here:
    }//GEN-LAST:event_tfcari1KeyReleased

    private void tfcariFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfcariFocusGained
setBorder(panelSearch);        // TODO add your handling code here:
    }//GEN-LAST:event_tfcariFocusGained

    private void tfcariFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfcariFocusLost
resetBorder(panelSearch);        // TODO add your handling code here:
    }//GEN-LAST:event_tfcariFocusLost

    private void tfcari1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfcari1FocusGained
setBorder(panelSearch1);         // TODO add your handling code here:
    }//GEN-LAST:event_tfcari1FocusGained

    private void tfcari1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfcari1FocusLost
resetBorder(panelSearch1);        // TODO add your handling code here:
    }//GEN-LAST:event_tfcari1FocusLost

    private void jLabel27MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel27MouseClicked
bMasterPressed();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel27MouseClicked

    private void btnTambah2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah2MouseClicked
FrmAnESupplier fr = new FrmAnESupplier();  
fr.setField(true, "");
fr.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah2MouseClicked

    private void btnTambah2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah2MouseEntered
setItemColor(btnTambah2, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah2MouseEntered

    private void btnTambah2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah2MouseExited
setItemColor(btnTambah2, new Color(25, 181, 254));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah2MouseExited

    private void btnTambah2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah2MousePressed
setItemColor(btnTambah2, new Color(6, 166, 224));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah2MousePressed

    private void btnTambah2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah2MouseReleased
setItemColor(btnTambah2, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah2MouseReleased

    private void btnUbah2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah2MouseClicked
try{
    int countRows[] = tbl_supp.getSelectedRows();
    if(countRows.length != 0){        
        int row = tbl_supp.getSelectedRow();
        String code = tbl_supp.getValueAt(row, 1).toString();
        FrmAnESupplier fr = new FrmAnESupplier();  
        fr.setField(false, code);
        fr.setVisible(true);
    }else JOptionPane.showMessageDialog(null, "Pilih dulu datanya atuh mang!");    
}catch (HeadlessException e){
    JOptionPane.showMessageDialog(rootPane, e);
}        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah2MouseClicked

    private void btnUbah2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah2MouseEntered
setItemColor(btnUbah2, new Color(89,214,116));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah2MouseEntered

    private void btnUbah2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah2MouseExited
setItemColor(btnUbah2, new Color(33,191,115));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah2MouseExited

    private void btnUbah2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah2MousePressed
setItemColor(btnUbah2, new Color(14,176,85));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah2MousePressed

    private void btnUbah2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah2MouseReleased
setItemColor(btnUbah2, new Color(89,214,116));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah2MouseReleased

    private void btnHapus2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus2MouseClicked
try{
    int countRows[] = tbl_supp.getSelectedRows();
    if(countRows.length != 0){
        String kode;
        String nama;
        StringBuilder sb = new StringBuilder();
        for (int a=0;a<countRows.length;a++){
            int rows = countRows[a];
            kode = tbl_supp.getValueAt(rows, 1).toString();
            nama = tbl_supp.getValueAt(rows, 2).toString();
            sb.append(a+1).append(". ").append(kode).append(" ").append(nama).append("\n");       
        }
        int ok = JOptionPane.showConfirmDialog(null, "Anda yakin ingin menghapus data ini?\n\n"+sb+"\n", "Konfirmasi", JOptionPane.OK_CANCEL_OPTION);
        if(ok==0) {
            try {            
                String sql;                
                for (int i=0;i<countRows.length;i++){
                    int row = countRows[i];
                    String code = tbl_supp.getValueAt(row, 1).toString();
                    sql = "delete from supplier where kode_supplier = '"+code+"'";
                    Statement rs = conn.createStatement();
                    rs.executeUpdate(sql);
                }
                Alert al = new Alert("Deleted Successfully", "/img/ok.png", new Color(33,191,115));
                al.setVisible(true);
                al.fadeOut(al);
                al.setVisible(false);
                dataSupplier();
                tfcari2.requestFocus();
            }catch (HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }else JOptionPane.showMessageDialog(null, "Pilih dulu datanya atuh mang!");    
}catch (HeadlessException e){
    JOptionPane.showMessageDialog(rootPane, e);
}        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus2MouseClicked

    private void btnHapus2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus2MouseEntered
setItemColor(btnHapus2, new Color(255,115,99));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus2MouseEntered

    private void btnHapus2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus2MouseExited
setItemColor(btnHapus2, new Color(255,46,49));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus2MouseExited

    private void btnHapus2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus2MousePressed
setItemColor(btnHapus2, new Color(236,31,19));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus2MousePressed

    private void btnHapus2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus2MouseReleased
setItemColor(btnHapus2, new Color(255,155,99));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus2MouseReleased

    private void tfcari2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfcari2FocusGained
setBorder(panelSearch2);        // TODO add your handling code here:
    }//GEN-LAST:event_tfcari2FocusGained

    private void tfcari2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfcari2FocusLost
resetBorder(panelSearch2);         // TODO add your handling code here:
    }//GEN-LAST:event_tfcari2FocusLost

    private void tfcari2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfcari2KeyReleased
try{
    if (tfcari2.getText().length()!=0){
        dataSupplier();
        sum_cari2.setText(String.valueOf(tbl_supp.getRowCount()));
    }else {
        dataSupplier();
        sum_cari2.setText("0");
    }     
}catch (Exception e){
    JOptionPane.showMessageDialog(rootPane, e);
}         // TODO add your handling code here:
    }//GEN-LAST:event_tfcari2KeyReleased

    private void jLabel35MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel35MouseClicked
bMasterPressed();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel35MouseClicked

    private void btnTambah3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah3MouseClicked
FrmAnESatuan fr = new FrmAnESatuan();  
fr.setField(true, "");
fr.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah3MouseClicked

    private void btnTambah3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah3MouseEntered
setItemColor(btnTambah3, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah3MouseEntered

    private void btnTambah3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah3MouseExited
setItemColor(btnTambah3, new Color(25, 181, 254));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah3MouseExited

    private void btnTambah3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah3MousePressed
setItemColor(btnTambah3, new Color(6, 166, 224));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah3MousePressed

    private void btnTambah3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah3MouseReleased
setItemColor(btnTambah3, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah3MouseReleased

    private void btnUbah3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah3MouseClicked
try{
    int countRows[] = tbl_stn.getSelectedRows();
    if(countRows.length != 0){        
        int row = tbl_stn.getSelectedRow();
        String code = tbl_stn.getValueAt(row, 1).toString();
        FrmAnESatuan fr = new FrmAnESatuan();  
        fr.setField(false, code);
        fr.setVisible(true);
    }else JOptionPane.showMessageDialog(null, "Pilih dulu datanya atuh mang!");    
}catch (HeadlessException e){
    JOptionPane.showMessageDialog(rootPane, e);
}         // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah3MouseClicked

    private void btnUbah3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah3MouseEntered
setItemColor(btnUbah3, new Color(89,214,116));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah3MouseEntered

    private void btnUbah3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah3MouseExited
 setItemColor(btnUbah3, new Color(33,191,115));       // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah3MouseExited

    private void btnUbah3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah3MousePressed
setItemColor(btnUbah3, new Color(14,176,85));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah3MousePressed

    private void btnUbah3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah3MouseReleased
setItemColor(btnUbah3, new Color(89,214,116));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah3MouseReleased

    private void btnHapus3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus3MouseClicked
        try{
            int countRows[] = tbl_stn.getSelectedRows();
            if(countRows.length != 0){
                String kode;
                String nama;
                StringBuilder sb = new StringBuilder();
                for (int a=0;a<countRows.length;a++){
                    int rows = countRows[a];
                    kode = tbl_stn.getValueAt(rows, 1).toString();
                    nama = tbl_stn.getValueAt(rows, 2).toString();
                    sb.append(a+1).append(". ").append(kode).append(" ").append(nama).append("\n");
                }
                int ok = JOptionPane.showConfirmDialog(null, "Anda yakin ingin menghapus data ini?\n\n"+sb+"\n", "Konfirmasi", JOptionPane.OK_CANCEL_OPTION);
                if(ok==0) {
                    try {
                        String sql;
                        for (int i=0;i<countRows.length;i++){
                            int row = countRows[i];
                            String code = tbl_stn.getValueAt(row, 1).toString();
                            sql = "delete from satuan where kode_satuan = '"+code+"'";
                            Statement rs = conn.createStatement();
                            rs.executeUpdate(sql);
                        }
                        Alert al = new Alert("Deleted Successfully", "/img/ok.png", new Color(33,191,115));
                        al.setVisible(true);
                        al.fadeOut(al);
                        al.setVisible(false);
                        dataSatuan();
                        tfcari3.requestFocus();
                    }catch (HeadlessException | SQLException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    }
                }
            }else JOptionPane.showMessageDialog(null, "Pilih dulu datanya atuh mang!");
        }catch (HeadlessException e){
            JOptionPane.showMessageDialog(rootPane, e);
        }         // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus3MouseClicked

    private void btnHapus3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus3MouseEntered
setItemColor(btnHapus3, new Color(255,115,99));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus3MouseEntered

    private void btnHapus3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus3MouseExited
setItemColor(btnHapus3, new Color(255,46,49));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus3MouseExited

    private void btnHapus3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus3MousePressed
setItemColor(btnHapus3, new Color(236,31,19));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus3MousePressed

    private void btnHapus3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus3MouseReleased
setItemColor(btnHapus1, new Color(255,155,99));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus3MouseReleased

    private void tfcari3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfcari3FocusGained
setBorder(panelSearch3);        // TODO add your handling code here:
    }//GEN-LAST:event_tfcari3FocusGained

    private void tfcari3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfcari3FocusLost
resetBorder(panelSearch3);        // TODO add your handling code here:
    }//GEN-LAST:event_tfcari3FocusLost

    private void tfcari3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfcari3KeyReleased
try{
    if (tfcari3.getText().length()!=0){
        dataSatuan();
        sum_cari3.setText(String.valueOf(tbl_stn.getRowCount()));
    }else {
        dataSatuan();
        sum_cari3.setText("0");
    }     
}catch (Exception e){
    JOptionPane.showMessageDialog(rootPane, e);
}         // TODO add your handling code here:
    }//GEN-LAST:event_tfcari3KeyReleased

    private void jLabel43MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel43MouseClicked
bMasterPressed();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel43MouseClicked

    private void btnTambah4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah4MouseClicked
FrmAnEMerk fr = new FrmAnEMerk();  
fr.setField(true, "");
fr.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah4MouseClicked

    private void btnTambah4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah4MouseEntered
setItemColor(btnTambah4, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah4MouseEntered

    private void btnTambah4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah4MouseExited
setItemColor(btnTambah4, new Color(25, 181, 254));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah4MouseExited

    private void btnTambah4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah4MousePressed
setItemColor(btnTambah4, new Color(6, 166, 224));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah4MousePressed

    private void btnTambah4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah4MouseReleased
setItemColor(btnTambah4, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah4MouseReleased

    private void btnUbah4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah4MouseClicked
try{
    int countRows[] = tbl_merk.getSelectedRows();
    if(countRows.length != 0){        
        int row = tbl_merk.getSelectedRow();
        String code = tbl_merk.getValueAt(row, 1).toString();
        FrmAnEMerk fr = new FrmAnEMerk();  
        fr.setField(false, code);
        fr.setVisible(true);
    }else JOptionPane.showMessageDialog(null, "Pilih dulu datanya atuh mang!");    
}catch (HeadlessException e){
    JOptionPane.showMessageDialog(rootPane, e);
}        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah4MouseClicked

    private void btnUbah4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah4MouseEntered
setItemColor(btnUbah4, new Color(89,214,116));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah4MouseEntered

    private void btnUbah4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah4MouseExited
setItemColor(btnUbah4, new Color(33,191,115));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah4MouseExited

    private void btnUbah4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah4MousePressed
setItemColor(btnUbah4, new Color(14,176,85));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah4MousePressed

    private void btnUbah4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah4MouseReleased
setItemColor(btnUbah4, new Color(89,214,116));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah4MouseReleased

    private void btnHapus4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus4MouseClicked
try{
    int countRows[] = tbl_merk.getSelectedRows();
    if(countRows.length != 0){
        String kode;
        String nama;
        StringBuilder sb = new StringBuilder();
        for (int a=0;a<countRows.length;a++){
            int rows = countRows[a];
            kode = tbl_merk.getValueAt(rows, 1).toString();
            nama = tbl_merk.getValueAt(rows, 2).toString();
            sb.append(a+1).append(". ").append(kode).append(" ").append(nama).append("\n");       
        }
        int ok = JOptionPane.showConfirmDialog(null, "Anda yakin ingin menghapus data ini?\n\n"+sb+"\n", "Konfirmasi", JOptionPane.OK_CANCEL_OPTION);
        if(ok==0) {
            try {            
                String sql;                
                for (int i=0;i<countRows.length;i++){
                    int row = countRows[i];
                    String code = tbl_merk.getValueAt(row, 1).toString();
                    sql = "delete from merk where kode_merk = '"+code+"'";
                    Statement rs = conn.createStatement();
                    rs.executeUpdate(sql);
                }
                Alert al = new Alert("Deleted Successfully", "/img/ok.png", new Color(33,191,115));
                al.setVisible(true);
                al.fadeOut(al);
                al.setVisible(false);
                dataMerk();
                tfcari4.requestFocus();
            }catch (HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }else JOptionPane.showMessageDialog(null, "Pilih dulu datanya atuh mang!");    
}catch (HeadlessException e){
    JOptionPane.showMessageDialog(rootPane, e);
}        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus4MouseClicked

    private void btnHapus4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus4MouseEntered
setItemColor(btnHapus4, new Color(255,115,99));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus4MouseEntered

    private void btnHapus4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus4MouseExited
setItemColor(btnHapus4, new Color(255,46,49));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus4MouseExited

    private void btnHapus4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus4MousePressed
setItemColor(btnHapus4, new Color(236,31,19));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus4MousePressed

    private void btnHapus4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus4MouseReleased
setItemColor(btnHapus4, new Color(255,155,99));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus4MouseReleased

    private void tfcari4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfcari4FocusGained
setBorder(panelSearch4);        // TODO add your handling code here:
    }//GEN-LAST:event_tfcari4FocusGained

    private void tfcari4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfcari4FocusLost
resetBorder(panelSearch4);        // TODO add your handling code here:
    }//GEN-LAST:event_tfcari4FocusLost

    private void tfcari4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfcari4KeyReleased
try{
    if (tfcari4.getText().length()!=0){
        dataMerk();
        sum_cari4.setText(String.valueOf(tbl_merk.getRowCount()));
    }else {
        dataMerk();
        sum_cari4.setText("0");
    }     
}catch (Exception e){
    JOptionPane.showMessageDialog(rootPane, e);
}        // TODO add your handling code here:
    }//GEN-LAST:event_tfcari4KeyReleased

    private void jLabel51MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel51MouseClicked
bMasterPressed();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel51MouseClicked

    private void btnTambah5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah5MouseClicked
FrmAnEBarang fr = new FrmAnEBarang();  
fr.setField(true, "");
fr.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah5MouseClicked

    private void btnTambah5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah5MouseEntered
setItemColor(btnTambah5, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah5MouseEntered

    private void btnTambah5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah5MouseExited
setItemColor(btnTambah5, new Color(25, 181, 254));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah5MouseExited

    private void btnTambah5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah5MousePressed
setItemColor(btnTambah5, new Color(6, 166, 224));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah5MousePressed

    private void btnTambah5MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah5MouseReleased
setItemColor(btnTambah5, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah5MouseReleased

    private void btnUbah5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah5MouseClicked
try{
    int countRows[] = tBarang.getSelectedRows();
    if(countRows.length != 0){        
        int row = tBarang.getSelectedRow();
        String code = tBarang.getValueAt(row, 1).toString();
        FrmAnEBarang fr = new FrmAnEBarang();  
        fr.setField(false, code);
        fr.setVisible(true);
    }else JOptionPane.showMessageDialog(null, "Pilih dulu datanya atuh mang!");    
}catch (HeadlessException e){
    JOptionPane.showMessageDialog(rootPane, e);
}        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah5MouseClicked

    private void btnUbah5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah5MouseEntered
setItemColor(btnUbah5, new Color(89,214,116));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah5MouseEntered

    private void btnUbah5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah5MouseExited
setItemColor(btnUbah5, new Color(33,191,115));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah5MouseExited

    private void btnUbah5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah5MousePressed
setItemColor(btnUbah5, new Color(14,176,85));       // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah5MousePressed

    private void btnUbah5MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah5MouseReleased
setItemColor(btnUbah5, new Color(89,214,116));         // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah5MouseReleased

    private void btnHapus5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus5MouseClicked
try{
    int countRows[] = tBarang.getSelectedRows();
    if(countRows.length != 0){
        String kode;
        String nama;
        String merk;
        StringBuilder sb = new StringBuilder();
        for (int a=0;a<countRows.length;a++){
            int rows = countRows[a];
            kode = tBarang.getValueAt(rows, 1).toString();
            nama = tBarang.getValueAt(rows, 2).toString();
            merk = tBarang.getValueAt(rows, 3).toString();
            sb.append(a+1).append(". ").append(kode).append(" ").append(nama).append(" ").append(merk).append("\n");       
        }
        int ok = JOptionPane.showConfirmDialog(null, "Anda yakin ingin menghapus data ini?\n\n"+sb+"\n", "Konfirmasi", JOptionPane.OK_CANCEL_OPTION);
        if(ok==0) {
            try {            
                String sql;                
                for (int i=0;i<countRows.length;i++){
                    int row = countRows[i];
                    String code = tBarang.getValueAt(row, 1).toString();
                    sql = "delete from barang where kode_barang = '"+code+"'";
                    Statement rs = conn.createStatement();
                    rs.executeUpdate(sql);
                }
                Alert al = new Alert("Deleted Successfully", "/img/ok.png", new Color(33,191,115));
                al.setVisible(true);
                al.fadeOut(al);
                al.setVisible(false);
                dataBarang();
                tfcari5.requestFocus();
            }catch (HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }else JOptionPane.showMessageDialog(null, "Pilih dulu datanya atuh mang!");    
}catch (HeadlessException e){
    JOptionPane.showMessageDialog(rootPane, e);
}        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus5MouseClicked

    private void btnHapus5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus5MouseEntered
setItemColor(btnHapus5, new Color(255,115,99));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus5MouseEntered

    private void btnHapus5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus5MouseExited
setItemColor(btnHapus5, new Color(255,46,49));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus5MouseExited

    private void btnHapus5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus5MousePressed
setItemColor(btnHapus5, new Color(236,31,19));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus5MousePressed

    private void btnHapus5MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus5MouseReleased
setItemColor(btnHapus5, new Color(255,155,99));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus5MouseReleased

    private void tfcari5FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfcari5FocusGained
setBorder(panelSearch5);        // TODO add your handling code here:
    }//GEN-LAST:event_tfcari5FocusGained

    private void tfcari5FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfcari5FocusLost
resetBorder(panelSearch5);        // TODO add your handling code here:
    }//GEN-LAST:event_tfcari5FocusLost

    private void tfcari5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfcari5KeyReleased
try{
    if (tfcari5.getText().length()!=0){
        dataBarang();
        sum_cari5.setText(String.valueOf(tBarang.getRowCount()));
    }else {
        dataBarang();
        sum_cari5.setText("0");
    }     
}catch (Exception e){
    JOptionPane.showMessageDialog(rootPane, e);
}        // TODO add your handling code here:
    }//GEN-LAST:event_tfcari5KeyReleased

    private void cbBulan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBulan1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbBulan1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
if("Admin".equals(userLevel) || "Staf Kasir".equals(userLevel)) {
        try {
            //String path = "./src/main/LaporanPenjualanHarian.jasper"; // letakpenyimpanan report
            HashMap parameter = new HashMap();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tgl = sdf.format(tgl4.getDate());
            parameter.put("ptgl", tgl);
            
            InputStream path = getClass().getResourceAsStream("/main/LaporanPenjualanHarian.jasper");
            JasperPrint print = JasperFillManager.fillReport(path, parameter, conn);
            JasperViewer.viewReport(print, false);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(null, "Dokumen Tidak Ada " + ex);
        }  
}else JOptionPane.showMessageDialog(null, "Access Denied ! ");// TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
if("Admin".equals(userLevel) || "Staf Kasir".equals(userLevel)) {
        int a = cbBulan.getSelectedIndex() + 1;
        String nol="";
        if (String.valueOf(a).length()==1){
            nol="0";
        }else nol="";
        String bln = nol+String.valueOf(a);
        String thn = tahun.getText();
        try {
            //String path = "./src/main/LaporanPenjualanBulanan.jasper"; // letakpenyimpanan report
            HashMap parameter = new HashMap();
            parameter.put("pbulan", bln);
            parameter.put("ptahun", thn);
            
            InputStream path = getClass().getResourceAsStream("/main/LaporanPenjualanBulanan.jasper");
            JasperPrint print = JasperFillManager.fillReport(path, parameter, conn);
            JasperViewer.viewReport(print, false);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(null, "Dokumen Tidak Ada " + ex);
        }
} else JOptionPane.showMessageDialog(null, "Access Denied ! ");// TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
if("Admin".equals(userLevel) || "Staf Kasir".equals(userLevel)) {
    String thn = tftahun.getText();
    if(!"".equals(thn)){        
        try {
            //String path="./src/main/LaporanPenjualanTahunan.jasper"; // letakpenyimpanan report
            HashMap parameter = new HashMap();
            parameter.put("ptahun",thn);
            
            InputStream path = getClass().getResourceAsStream("/main/LaporanPenjualanTahunan.jasper");
            JasperPrint print = JasperFillManager.fillReport(path,parameter,conn);
            JasperViewer.viewReport(print, false);
        }catch (JRException ex) {
            JOptionPane.showMessageDialog(null,"Dokumen Tidak Ada "+ex);
       }
    }else JOptionPane.showMessageDialog(null, "Terdapat Inputan Kosong!"); 
}else JOptionPane.showMessageDialog(null, " Access Denied ! ");// TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
if("Admin".equals(userLevel) || "Staf Gudang".equals(userLevel)) {
        try {
            //String path = "./src/main/LaporanPembelianHarian.jasper"; // letakpenyimpanan report
            HashMap parameter = new HashMap();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tgl = sdf.format(tgl3.getDate());
            parameter.put("ptgl", tgl);
            
            InputStream path = getClass().getResourceAsStream("/main/LaporanPembelianHarian.jasper");
            JasperPrint print = JasperFillManager.fillReport(path, parameter, conn);
            JasperViewer.viewReport(print, false);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(null, "Dokumen Tidak Ada " + ex);
        }
} else JOptionPane.showMessageDialog(null, " Access Denied !");// TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
if("Admin".equals(userLevel) || "Staf Gudang".equals(userLevel)) {
        int a = cbBulan1.getSelectedIndex() + 1;
        String nol="";
        if (String.valueOf(a).length()==1){
            nol="0";
        }else nol="";
        String bln = nol+String.valueOf(a);
        String thn = tahun1.getText();
        try {
            //String path = "./src/main/LaporanPembelianBulanan.jasper"; // letakpenyimpanan report
            HashMap parameter = new HashMap();
            parameter.put("pbulan", bln);
            parameter.put("ptahun", thn);
            
            InputStream path = getClass().getResourceAsStream("/main/LaporanPembelianBulanan.jasper");
            JasperPrint print = JasperFillManager.fillReport(path, parameter, conn);
            JasperViewer.viewReport(print, false);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(null, "Dokumen Tidak Ada " + ex);
        }
} else JOptionPane.showMessageDialog(null, " Access Denied !");        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
if("Admin".equals(userLevel) || "Staf Gudang".equals(userLevel)) {
    String thn = tftahun1.getText();
    if(!"".equals(thn)){        
        try {
            //String path="./src/main/LaporanPembelianTahunan.jasper"; // letakpenyimpanan report
            HashMap parameter = new HashMap();
            parameter.put("ptahun",thn);
            
            InputStream path = getClass().getResourceAsStream("/main/LaporanPembelianTahunan.jasper");
            JasperPrint print = JasperFillManager.fillReport(path,parameter,conn);
            JasperViewer.viewReport(print, false);
        }catch (JRException ex) {
            JOptionPane.showMessageDialog(null,"Dokumen Tidak Ada "+ex);
       }
    }else JOptionPane.showMessageDialog(null, "Terdapat Inputan Kosong!");
} else JOptionPane.showMessageDialog(null, " Access Denied !");// TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
if("Admin".equals(userLevel) || "Staf Gudang".equals(userLevel)) {
        try {
            //String path="./src/main/LaporanStokBarang.jasper"; // letakpenyimpanan report
            HashMap parameter = new HashMap();
            
            InputStream path = getClass().getResourceAsStream("/main/LaporanStokBarang.jasper");
            JasperPrint print = JasperFillManager.fillReport(path,parameter,conn);
            JasperViewer.viewReport(print, false);
       } catch (JRException ex) {
            JOptionPane.showMessageDialog(null,"DokumenTidak Ada "+ex);
       }   
} else JOptionPane.showMessageDialog(null, " Access Denied !");// TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
if("Admin".equals(userLevel) || "Staf Kasir".equals(userLevel)) {
        try {
            //String path="./src/main/Laporan10BarangTerlaris.jasper"; // letakpenyimpanan report
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tgl1 = sdf.format(xtgl1.getDate());
            String tgl2 = sdf.format(xtgl2.getDate());
            HashMap parameter = new HashMap();
            parameter.put("tgl1",tgl1);
            parameter.put("tgl2",tgl2);
            
            InputStream path = getClass().getResourceAsStream("/main/Laporan10BarangTerlaris.jasper");
            JasperPrint print = JasperFillManager.fillReport(path,parameter,conn);
            JasperViewer.viewReport(print, false);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(null,"DokumenTidak Ada "+ex);
       } 
} else JOptionPane.showMessageDialog(null, " Access Denied !");        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void btnTambah6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah6MouseEntered
setItemColor(btnTambah6, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah6MouseEntered

    private void btnTambah6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah6MouseExited
setItemColor(btnTambah6, new Color(25, 181, 254));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah6MouseExited

    private void btnTambah6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah6MousePressed
setItemColor(btnTambah6, new Color(6, 166, 224));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah6MousePressed

    private void btnTambah6MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah6MouseReleased
setItemColor(btnTambah6, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah6MouseReleased

    private void btnUbah6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah6MouseEntered
setItemColor(btnUbah6, new Color(89,214,116));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah6MouseEntered

    private void btnUbah6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah6MouseExited
setItemColor(btnUbah6, new Color(33,191,115));       // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah6MouseExited

    private void btnUbah6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah6MousePressed
 setItemColor(btnUbah6, new Color(14,176,85));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah6MousePressed

    private void btnUbah6MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah6MouseReleased
setItemColor(btnUbah6, new Color(89,214,116));        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah6MouseReleased

    private void btnHapus6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus6MouseEntered
setItemColor(btnHapus6, new Color(255,115,99));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus6MouseEntered

    private void btnHapus6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus6MouseExited
setItemColor(btnHapus6, new Color(255,46,49));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus6MouseExited

    private void btnHapus6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus6MousePressed
setItemColor(btnHapus6, new Color(236,31,19));        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus6MousePressed

    private void btnHapus6MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus6MouseReleased
 setItemColor(btnHapus6, new Color(255,155,99));       // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus6MouseReleased

    private void btnSimpan6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpan6MouseEntered
setItemColor(btnSimpan6, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpan6MouseEntered

    private void btnSimpan6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpan6MouseExited
setItemColor(btnSimpan6, new Color(25, 181, 254));        // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpan6MouseExited

    private void btnSimpan6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpan6MousePressed
setItemColor(btnSimpan6, new Color(6, 166, 224));       // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpan6MousePressed

    private void btnSimpan6MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpan6MouseReleased
setItemColor(btnSimpan6, new Color(81, 204, 255));       // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpan6MouseReleased

    private void tUsersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tUsersMouseClicked
lbAksi.setText("Tambah Data");
kosongUser();
try{
    int row = tUsers.getSelectedRow();
    String code = tUsers.getValueAt(row, 1).toString();
    ResultSet res = conn.createStatement().executeQuery("select foto from pegawai where kode_pegawai= '"+code+"'");
while(res.next()){
    uImg.setIcon(new ImageIcon(new ImageIcon(res.getBytes("foto")).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
    
}    
}catch(SQLException e){
    JOptionPane.showMessageDialog(null, "Error"+e);
}
    // TODO add your handling code here:
    }//GEN-LAST:event_tUsersMouseClicked

    private void btnTambah6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambah6MouseClicked
tUsers.clearSelection();
lbAksi.setText("Tambah Data");
kosongUser();
tfuname.requestFocus();// TODO add your handling code here:
    }//GEN-LAST:event_btnTambah6MouseClicked

    private void btnUbah6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUbah6MouseClicked
        int countRows[] = tUsers.getSelectedRows();
        if (countRows.length != 0) {
            lbAksi.setText("Ubah Data");
            try {
                int baris = tUsers.getSelectedRow();
                String kdUsr = tUsers.getValueAt(baris, 1).toString();
                cbKdUsr.setSelectedItem(kdUsr);
                txtkode_temp.setText(kdUsr);
                String nmUsr = tUsers.getValueAt(baris, 2).toString();
                tfnama.setText(nmUsr);
                String unUsr = tUsers.getValueAt(baris, 3).toString();
                tfuname.setText(unUsr);
                String pass = tUsers.getValueAt(baris, 4).toString();
                tfpass.setText(pass);
                String lvl = tUsers.getValueAt(baris, 5).toString();
                tfLevel.setText(lvl);
                tfuname.requestFocus();
            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pilih dulu datanya atuh mang!");
        }
    // TODO add your handling code here:
    }//GEN-LAST:event_btnUbah6MouseClicked

    private void btnSimpan6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpan6MouseClicked
try{
    String txtkodetemp = txtkode_temp.getText();
    if("".equals(tfuname.getText().trim()) ||"".equals(tfpass.getText().trim())){
        if("".equals(tfuname.getText().trim())){
            Warn(uluname, new Color(255, 46, 49), iuname, "/img/warn.png");
        }if("".equals(tfpass.getText().trim())){
            Warn(ulpass, new Color(255, 46, 49), ipass, "/img/warn.png");
        }
    }else{
        if("".equals(txtkodetemp)){
            try {
                String sql = "INSERT INTO users VALUES ('"+cbKdUsr.getSelectedItem().toString()+"','"+tfuname.getText()+"','"+tfpass.getText()+"')";
                PreparedStatement pst=conn.prepareStatement(sql);
                pst.execute();
                dataUsers();
                Alert al = new Alert("Saved Successfully", "/img/ok.png", new Color(33,191,115));
                al.setVisible(true);
                al.fadeOut(al);
                al.dispose();
                kosongUser();
                tfuname.requestFocus();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Penyimpanan Data Gagal"+e);
            }
        }else if(txtkodetemp.equals(cbKdUsr.getSelectedItem().toString())){
            try {
                PreparedStatement pst=conn.prepareStatement("UPDATE users SET username =?, password =? WHERE kode_pegawai = '"+cbKdUsr.getSelectedItem().toString()+"'");
                pst.setString(1, tfuname.getText());
                pst.setString(2, tfpass.getText());
                pst.executeUpdate();
                Alert al = new Alert("Saved Successfully", "/img/ok.png", new Color(33,191,115));
                al.setVisible(true);
                al.fadeOut(al);
                al.dispose();
                kosongUser();
                dataUsers();
                tfuname.requestFocus();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Perubahan data Gagal!"+ex.getMessage());
            }               
        }
    }
        
        
}catch (HeadlessException e){
    JOptionPane.showMessageDialog(rootPane, e);
}        // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpan6MouseClicked

    private void cbKdUsrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbKdUsrActionPerformed
    try {
        String sql="select * from pegawai where kode_pegawai=?";
        PreparedStatement ps  = conn.prepareStatement(sql);
        ps.setString(1, (String)cbKdUsr.getSelectedItem());
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
            tfnama.setText(rs.getString("nama_pegawai"));
            tfLevel.setText(rs.getString("jabatan"));
            uImg.setIcon(new ImageIcon(new ImageIcon(rs.getBytes("foto")).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
            tfuname.requestFocus();
        }
   } catch (SQLException e) {
       e.getMessage();
   }        // TODO add your handling code here:
    }//GEN-LAST:event_cbKdUsrActionPerformed

    private void tfunameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfunameKeyReleased
if(tfuname.getText().length()!=0){
    Warn(uluname, new Color(218, 218, 218), iuname, "");
}else Warn(uluname, new Color(255, 46, 49), iuname, "/img/warn.png");        // TODO add your handling code here:
    }//GEN-LAST:event_tfunameKeyReleased

    private void tfpassKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfpassKeyReleased
if(tfpass.getText().length()!=0){
    Warn(ulpass, new Color(218, 218, 218), ipass, "");
}else Warn(ulpass, new Color(255, 46, 49), ipass, "/img/warn.png");        // TODO add your handling code here:
    }//GEN-LAST:event_tfpassKeyReleased

    private void btnHapus6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapus6MouseClicked
try{
    int countRows[] = tUsers.getSelectedRows();
    if(countRows.length != 0){
        String kode;
        String nama;
        StringBuilder sb = new StringBuilder();
        for (int a=0;a<countRows.length;a++){
            int rows = countRows[a];
            kode = tUsers.getValueAt(rows, 1).toString();
            nama = tUsers.getValueAt(rows, 2).toString();
            sb.append(a+1).append(". ").append(kode).append(" ").append(nama).append("\n");       
        }
        int ok = JOptionPane.showConfirmDialog(null, "Anda yakin ingin menghapus data ini?\n\n"+sb+"\n", "Konfirmasi", JOptionPane.OK_CANCEL_OPTION);
        if(ok==0) {
            try {            
                String sql;                
                for (int i=0;i<countRows.length;i++){
                    int row = countRows[i];
                    String code = tUsers.getValueAt(row, 1).toString();
                    sql = "delete from users where kode_pegawai = '"+code+"'";
                    Statement rs = conn.createStatement();
                    rs.executeUpdate(sql);
                }
                Alert al = new Alert("Deleted Successfully", "/img/ok.png", new Color(33,191,115));
                al.setVisible(true);
                al.fadeOut(al);
                al.setVisible(false);
                kosongUser();
                dataUsers();                
            }catch (HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }else JOptionPane.showMessageDialog(null, "Pilih dulu datanya atuh mang!");    
}catch (HeadlessException e){
    JOptionPane.showMessageDialog(rootPane, e);        
    }        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapus6MouseClicked

    private void btnBatalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBatalMouseClicked
kosongUser();
tUsers.clearSelection();// TODO add your handling code here:
    }//GEN-LAST:event_btnBatalMouseClicked

    private void bCetakMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bCetakMouseEntered
setItemColor(btnCetak, new Color(81, 204, 255));         // TODO add your handling code here:
    }//GEN-LAST:event_bCetakMouseEntered

    private void bCetakMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bCetakMouseExited
setItemColor(btnCetak, new Color(25, 181, 254));       // TODO add your handling code here:
    }//GEN-LAST:event_bCetakMouseExited

    private void bCetakMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bCetakMousePressed
setItemColor(btnCetak, new Color(6, 166, 224));        // TODO add your handling code here:
    }//GEN-LAST:event_bCetakMousePressed

    private void bCetakMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bCetakMouseReleased
setItemColor(btnCetak, new Color(81, 204, 255));         // TODO add your handling code here:
    }//GEN-LAST:event_bCetakMouseReleased

    private void bCetakMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bCetakMouseClicked
String limit = tflimit.getText();
if(!"".equals(limit)){        
    try {
        int lmt = Integer.valueOf(limit);
        //String path="./src/main/LaporanRestokBarang.jasper"; // letakpenyimpanan report
        HashMap parameter = new HashMap();
        parameter.put("limit",lmt);
        
        InputStream path = getClass().getResourceAsStream("/main/LaporanRestokBarang.jasper");
        JasperPrint print = JasperFillManager.fillReport(path,parameter,conn);
        JasperViewer.viewReport(print, false);
    }catch (JRException ex) {
        JOptionPane.showMessageDialog(null,"Dokumen Tidak Ada "+ex);
   }
}else JOptionPane.showMessageDialog(null, "Terdapat Inputan Kosong!");           // TODO add your handling code here:
    }//GEN-LAST:event_bCetakMouseClicked

    private void tflimitKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tflimitKeyReleased
try{
    if (tflimit.getText().length()!=0){
        dataStok();
    }else {
        dataStok();
    }     
}catch (Exception e){
    JOptionPane.showMessageDialog(rootPane, e);
}         // TODO add your handling code here:
    }//GEN-LAST:event_tflimitKeyReleased

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
new TrxPenjualan().setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton9ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Dashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel act1;
    private javax.swing.JPanel act2;
    private javax.swing.JPanel act3;
    private javax.swing.JPanel act4;
    private javax.swing.JPanel act5;
    private javax.swing.JPanel act6;
    private javax.swing.JLabel admName;
    private javax.swing.JLabel bCetak;
    private javax.swing.JPanel bDash;
    private javax.swing.JPanel bKeluar;
    private javax.swing.JPanel bLap;
    private javax.swing.JPanel bMUser;
    private javax.swing.JPanel bMaster;
    private javax.swing.JPanel bTrans;
    private javax.swing.JPanel barang;
    private javax.swing.JPanel beli;
    private javax.swing.JPanel bg;
    private javax.swing.JPanel bg1;
    private javax.swing.JPanel bg2;
    private javax.swing.JPanel bg3;
    private javax.swing.JPanel bg4;
    private javax.swing.JPanel bg5;
    private javax.swing.JPanel bg6;
    private javax.swing.JPanel bgbeli;
    private javax.swing.JPanel bgjual;
    private javax.swing.JPanel bgmain;
    private javax.swing.JPanel btnBatal;
    private javax.swing.JPanel btnCetak;
    private javax.swing.JPanel btnHapus;
    private javax.swing.JPanel btnHapus1;
    private javax.swing.JPanel btnHapus2;
    private javax.swing.JPanel btnHapus3;
    private javax.swing.JPanel btnHapus4;
    private javax.swing.JPanel btnHapus5;
    private javax.swing.JPanel btnHapus6;
    private javax.swing.JPanel btnSimpan6;
    private javax.swing.JPanel btnTambah;
    private javax.swing.JPanel btnTambah1;
    private javax.swing.JPanel btnTambah2;
    private javax.swing.JPanel btnTambah3;
    private javax.swing.JPanel btnTambah4;
    private javax.swing.JPanel btnTambah5;
    private javax.swing.JPanel btnTambah6;
    private javax.swing.JPanel btnUbah;
    private javax.swing.JPanel btnUbah1;
    private javax.swing.JPanel btnUbah2;
    private javax.swing.JPanel btnUbah3;
    private javax.swing.JPanel btnUbah4;
    private javax.swing.JPanel btnUbah5;
    private javax.swing.JPanel btnUbah6;
    private javax.swing.JComboBox<String> cbBulan;
    private javax.swing.JComboBox<String> cbBulan1;
    private javax.swing.JComboBox<String> cbKdUsr;
    private javax.swing.JPanel dashAdmin;
    private javax.swing.JPanel dashGudang;
    private javax.swing.JPanel dashKasir;
    private javax.swing.JLabel exit;
    private javax.swing.JLabel iconBarang;
    private javax.swing.JLabel iconBuy;
    private javax.swing.JLabel iconMerk;
    private javax.swing.JLabel iconPegawai;
    private javax.swing.JLabel iconPelanggan;
    private javax.swing.JLabel iconSatuan;
    private javax.swing.JLabel iconSell;
    private javax.swing.JLabel iconSupplier;
    private javax.swing.JLabel imgUser;
    private javax.swing.JLabel ipass;
    private javax.swing.JLabel iuname;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel100;
    private javax.swing.JPanel jPanel101;
    private javax.swing.JPanel jPanel102;
    private javax.swing.JPanel jPanel103;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel44;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel47;
    private javax.swing.JPanel jPanel48;
    private javax.swing.JPanel jPanel49;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel50;
    private javax.swing.JPanel jPanel51;
    private javax.swing.JPanel jPanel52;
    private javax.swing.JPanel jPanel53;
    private javax.swing.JPanel jPanel54;
    private javax.swing.JPanel jPanel55;
    private javax.swing.JPanel jPanel56;
    private javax.swing.JPanel jPanel57;
    private javax.swing.JPanel jPanel58;
    private javax.swing.JPanel jPanel59;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel60;
    private javax.swing.JPanel jPanel61;
    private javax.swing.JPanel jPanel62;
    private javax.swing.JPanel jPanel63;
    private javax.swing.JPanel jPanel64;
    private javax.swing.JPanel jPanel65;
    private javax.swing.JPanel jPanel66;
    private javax.swing.JPanel jPanel67;
    private javax.swing.JPanel jPanel68;
    private javax.swing.JPanel jPanel69;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel70;
    private javax.swing.JPanel jPanel71;
    private javax.swing.JPanel jPanel72;
    private javax.swing.JPanel jPanel73;
    private javax.swing.JPanel jPanel74;
    private javax.swing.JPanel jPanel75;
    private javax.swing.JPanel jPanel76;
    private javax.swing.JPanel jPanel77;
    private javax.swing.JPanel jPanel78;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel87;
    private javax.swing.JPanel jPanel88;
    private javax.swing.JPanel jPanel89;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanel90;
    private javax.swing.JPanel jPanel91;
    private javax.swing.JPanel jPanel92;
    private javax.swing.JPanel jPanel94;
    private javax.swing.JPanel jPanel95;
    private javax.swing.JPanel jPanel96;
    private javax.swing.JPanel jPanel97;
    private javax.swing.JPanel jPanel98;
    private javax.swing.JPanel jPanel99;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel jb;
    private javax.swing.JLabel jm;
    private javax.swing.JLabel jmlUsr;
    private static javax.swing.JLabel jml_brg;
    private static javax.swing.JLabel jml_merk;
    private static javax.swing.JLabel jml_pgw;
    private static javax.swing.JLabel jml_plg;
    private static javax.swing.JLabel jml_stn;
    private javax.swing.JLabel jml_stok;
    private static javax.swing.JLabel jml_supp;
    private javax.swing.JLabel jpeg;
    private javax.swing.JLabel jpel;
    private javax.swing.JLabel jsat;
    private javax.swing.JLabel jsup;
    private javax.swing.JPanel jual;
    private javax.swing.JPanel lap;
    private javax.swing.JLabel lbAksi;
    private javax.swing.JLabel lb_barang;
    private javax.swing.JLabel lb_buy;
    private javax.swing.JLabel lb_merk;
    private javax.swing.JLabel lb_pegawai;
    private javax.swing.JLabel lb_pelanggan;
    private javax.swing.JLabel lb_satuan;
    private javax.swing.JLabel lb_sell;
    private javax.swing.JLabel lb_supplier;
    private javax.swing.JLabel lbl_level;
    private javax.swing.JLabel lbl_name;
    private javax.swing.JLabel ldash;
    private javax.swing.JLabel lkeluar;
    private javax.swing.JLabel llap;
    private javax.swing.JLabel lmaster;
    private javax.swing.JLabel lmuser;
    private javax.swing.JLabel ltrans;
    private javax.swing.JPanel main;
    private javax.swing.JPanel mainpane;
    private javax.swing.JPanel master;
    private javax.swing.JPanel master_barang;
    private javax.swing.JPanel master_merk;
    private javax.swing.JPanel master_pegawai;
    private javax.swing.JPanel master_pelanggan;
    private javax.swing.JPanel master_satuan;
    private javax.swing.JPanel master_supplier;
    private javax.swing.JPanel merk;
    private javax.swing.JLabel min;
    private javax.swing.JPanel mngUser;
    private javax.swing.JPanel panelSearch;
    private javax.swing.JPanel panelSearch1;
    private javax.swing.JPanel panelSearch2;
    private javax.swing.JPanel panelSearch3;
    private javax.swing.JPanel panelSearch4;
    private javax.swing.JPanel panelSearch5;
    private javax.swing.JPanel pegawai;
    private javax.swing.JPanel pelanggan;
    private javax.swing.JLabel ptgsName;
    private javax.swing.JLabel ptgsName1;
    private javax.swing.JPanel satuan;
    private javax.swing.JPanel sidepane;
    private javax.swing.JLabel sum_cari;
    private javax.swing.JLabel sum_cari1;
    private javax.swing.JLabel sum_cari2;
    private javax.swing.JLabel sum_cari3;
    private javax.swing.JLabel sum_cari4;
    private javax.swing.JLabel sum_cari5;
    private javax.swing.JPanel supplier;
    private static javax.swing.JTable tBarang;
    private javax.swing.JTable tStok;
    private javax.swing.JTable tUsers;
    private javax.swing.JLabel tahun;
    private javax.swing.JLabel tahun1;
    private static javax.swing.JTable tbl_merk;
    private static javax.swing.JTable tbl_pgw;
    private static javax.swing.JTable tbl_plg;
    private static javax.swing.JTable tbl_stn;
    private static javax.swing.JTable tbl_supp;
    private javax.swing.JTextField tfLevel;
    private javax.swing.JLabel tfadate;
    private javax.swing.JLabel tfatime;
    private static javax.swing.JTextField tfcari;
    private static javax.swing.JTextField tfcari1;
    private static javax.swing.JTextField tfcari2;
    private static javax.swing.JTextField tfcari3;
    private static javax.swing.JTextField tfcari4;
    private static javax.swing.JTextField tfcari5;
    private javax.swing.JTextField tflimit;
    private javax.swing.JTextField tfnama;
    private javax.swing.JTextField tfpass;
    private javax.swing.JTextField tftahun;
    private javax.swing.JTextField tftahun1;
    private javax.swing.JTextField tfuname;
    private com.toedter.calendar.JDateChooser tgl3;
    private com.toedter.calendar.JDateChooser tgl4;
    private javax.swing.JPanel trans;
    private javax.swing.JTextField txtkode_temp;
    private javax.swing.JLabel uImg;
    private javax.swing.JPanel ulnama;
    private javax.swing.JPanel ulpass;
    private javax.swing.JPanel uluname;
    private com.toedter.calendar.JDateChooser xtgl1;
    private com.toedter.calendar.JDateChooser xtgl2;
    // End of variables declaration//GEN-END:variables
    public void Warn(JPanel pn, Color c, JLabel lb, String a){
        pn.setBackground(c);
        setIcon(lb, a);
    }

    void setBorder(JPanel panel){
        Border border = BorderFactory.createLineBorder(new Color(25, 181, 254));
        panel.setBorder(border);
    }
    
    void resetBorder(JPanel panel){
        Border border = BorderFactory.createLineBorder(new Color(218,218,218));
        panel.setBorder(border);
    }
    
    void setColor(JPanel panel, JPanel panel2, JLabel label){
        panel.setBackground(new Color(235, 248, 252));
        panel2.setBackground(new Color(56, 175, 233));
        label.setForeground(new Color(56, 175, 233));
    }
    
    void resetColor(JPanel panel, JPanel panel2, JLabel label){
        panel.setBackground(new Color(255,255,255));
        panel2.setBackground(new Color(255,255,255));
        label.setForeground(new Color(188, 188, 188));
    }
    
    void setIcon(JLabel label, String path){
        label.setIcon(new javax.swing.ImageIcon(getClass().getResource(path)));
    }
    
    void setIconMenu(JLabel lb1, String path, JLabel lb2, Color c){
        lb1.setIcon(new javax.swing.ImageIcon(getClass().getResource(path)));
        lb2.setForeground(c);
    }

    void setPanelVisible( JPanel p1, JPanel p2, JPanel p3, JPanel p4, JPanel p5, JPanel p6, Boolean x){
        p1.setVisible(x);
        p2.setVisible(x);
        p3.setVisible(x);
        p4.setVisible(x);
        p5.setVisible(x);
        p6.setVisible(x);
    }
        
    void setItemColor(JPanel panel, Color c){
        panel.setBackground(c);
    }

    
    public int roundUp(int input){
        return ((input+99)/100)*100;
    }
        
    public class DropShadowPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        public int pixels;
        public int r;
        public int x;
        public int b;
        public DropShadowPanel(int pix, int r, int g, int b){
            this.pixels = pix;
            this.r = r;
            this.x = g;
            this.b = b;
            Border border = BorderFactory.createEmptyBorder(pixels, pixels, pixels, pixels);
            this.setBorder(BorderFactory.createCompoundBorder(this.getBorder(), border));
            this.setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g){
            int topOpacity = 80;
            for (int i=0; i< pixels; i++){
                g.setColor(new Color(r, x, b, ((topOpacity / pixels)*i)));
                g.drawRect(i, i, this.getWidth() - ((i*2)+1), this.getHeight() - ((i*2)+1));
            }
        }
    }
}
