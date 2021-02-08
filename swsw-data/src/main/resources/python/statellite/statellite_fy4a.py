import meteva.base as meb
import numpy as np
import datetime
import xarray as xr
import os
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.pyplot import savefig
from PIL import Image
import sys
import netCDF4 as nc
from mpl_toolkits.basemap import Basemap
import maskout

def create_statellite_contourf(filepath, savedir, lon_min=70.0, lon_max=140.0, lat_min=15.0, lat_max=55.0):
    dataset = nc.Dataset(filepath, 'r')
    # 纬度
    lat_orignal = dataset.variables["lat"][:]
    print(lat_orignal)
    # 经度
    lon_orignal = dataset.variables["lon"][:]
    # 值
    tem = dataset.variables['data0'][:][0][0][0][0]
    # 切割得到位于范围内的经纬度列表
    lon_idx = np.where((lon_orignal >= float(lon_min)) & (lon_orignal <= float(lon_max)))
    lon_after = lon_orignal[lon_idx]
    lat_idx = np.where((lat_orignal >= float(lat_min)) & (lat_orignal <= float(lat_max)))
    lat_after = lat_orignal[lat_idx]
    lon_min_ind = lon_idx[0][0]
    lon_max_ind = lon_idx[0][-1]
    lat_min_ind = lat_idx[0][0]
    lat_max_ind = lat_idx[0][-1]
    # 生成网格数据
    xx, yy = np.meshgrid(lon_after, lat_after)
    # xx, yy = np.meshgrid(lon_orignal, lat_orignal)
    tem_after = tem[lat_min_ind:lat_max_ind + 1, lon_min_ind: lon_max_ind + 1]
    lon_min = lon_after[0]
    lon_max = lon_after[-1]
    lat_min = lat_after[0]
    lat_max = lat_after[-1]
    fig = plt.figure(figsize=(10, 10))
    # m = Basemap(llcrnrlon=lon_min,llcrnrlat=lat_max,urcrnrlon=lon_max,urcrnrlat=lat_min,projection = 'cyl')
    # m = Basemap(llcrnrlon=lon_min,llcrnrlat=lat_min,urcrnrlon=lon_max,urcrnrlat=lat_max,projection = 'cyl')
    # m = Basemap(width=12000000,height=8000000,resolution='l',projection='laea',\
    #             lat_ts=10, lat_0=27.5, lon_0=105.)
    m = Basemap(projection='merc', llcrnrlat=lat_min, urcrnrlat=lat_max, \
                llcrnrlon=lon_min, urcrnrlon=lon_max, lat_ts=5, resolution='c')
    ax = plt.subplot(1, 1, 1)  # 绘图区
    plt.axis('off')
    # m.drawparallels(np.arange(-80., 81., 20.))
    # m.drawmeridians(np.arange(-180., 181., 20.))
    cs = m.contourf(xx, yy, tem_after, latlon=True)
    # clip = maskout.shp2clip(cs, ax, m, 'cn/country1', 'China')
    filename_statellite = filepath.split('/')[-1]
    filename_img = filename_statellite.split('.')[0] + '.svg'
    if os.path.exists(savedir) == False:
        os.makedirs(savedir)
    savepath = savedir + '/' + filename_img
    plt.savefig(savepath, transparent=True, bbox_inches='tight', pad_inches=0)

if __name__ == '__main__':
    filepath = sys.argv[1]
    savepath = sys.argv[2]
    lon_min = sys.argv[3]
    lon_max = sys.argv[4]
    lat_min = sys.argv[5]
    lat_max = sys.argv[6]
    # filepath = r'D:/Data/satellite_base/FY4A/20201204/CHANNEL02/C002_20201204101918_FY4A.AWX'
    # savepath = r'D:/Data/satellite_parse/FY4A/20201127/CHANNEL12'
    # lon_min = 90
    # lon_max = 100
    # lat_min = 30
    # lat_max = 50
    create_statellite_contourf(filepath, savepath, lon_min, lon_max, lat_min, lat_max)

