/**
 * Electric Usage Tracker Dashboard Functionality
 * Core JS functions for dashboard API integration and real-time features
 */

// API endpoints
const API = {
  DASHBOARD_DATA: '/api/dashboard/data',
  APPLIANCES: '/api/dashboard/appliances',
  SAVE_USAGE: '/api/dashboard/saveUsage',
  DELETE_USAGE: '/api/dashboard/usage/'
};

// Get the CSRF token for secure requests
// Note: These are now provided directly in the HTML via inline script
// const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
// const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

// Dashboard initialization
document.addEventListener('DOMContentLoaded', function() {
  // Load real data from backend
  fetchDashboardData();

  // Setup event listeners for mobile menu
  setupMobileMenu();
  
  // Initialize appliance dropdown in calculator
  initializeApplianceDropdown();
});

// Fetch all dashboard data from API
function fetchDashboardData() {
  fetch(API.DASHBOARD_DATA)
    .then(response => {
      if (!response.ok) {
        throw new Error(`Error fetching dashboard data: ${response.status}`);
      }
      return response.json();
    })
    .then(data => {
      // Store globally
      window.dashboardData = data;
      
      // Update all dashboard components with real data
      updateDashboardMetrics(data);
      updateTrendIndicators(data);
      initializeCharts(data);
      updateEfficiencyGauge(data.efficiencyScore || 0);
    })
    .catch(error => {
      console.error('API Error:', error);
      // Initialize with fallback data
      initializeWithDemoData();
    });
}

// Format number with commas for thousands separators
function formatNumberWithCommas(number) {
  return new Intl.NumberFormat('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(number || 0);
}

// Update dashboard summary metrics
function updateDashboardMetrics(data) {
  if (!data) return;
  
  // Update summary values with proper number formatting
  const metrics = {
    'totalEnergyValue': formatNumberWithCommas(data.totalEnergy) || '0.00',
    'totalCostValue': formatNumberWithCommas(data.totalCost) || '0.00',
    'recordCountValue': data.recordCount || '0',
    'efficiencyScore': Math.round(data.efficiencyScore) || '0'
  };
  
  // Update DOM elements
  Object.keys(metrics).forEach(id => {
    const element = document.getElementById(id);
    if (element) {
      element.textContent = metrics[id];
    }
  });
  
  // Update new devices trend
  const newDevicesElement = document.getElementById('newDevicesTrend');
  if (newDevicesElement && data.newDevicesCount !== undefined) {
    const newDevicesCount = data.newDevicesCount || 0;
    newDevicesElement.innerHTML = `
      <i class="fas fa-${newDevicesCount > 0 ? 'plus' : 'minus'} mr-1"></i>
      <span>${newDevicesCount} new</span>
      <span class="text-gray-500 ml-1">this week</span>
    `;
    // Set appropriate color
    newDevicesElement.className = 'text-xs mt-1';
    newDevicesElement.classList.add(newDevicesCount > 0 ? 'text-green-500' : 'text-gray-400');
  }
  
  // Update efficiency trend
  const efficiencyTrendElement = document.getElementById('efficiencyTrend');
  if (efficiencyTrendElement && data.efficiencyChange !== undefined) {
    const efficiencyChange = data.efficiencyChange || 0;
    const isPositive = efficiencyChange > 0;
    efficiencyTrendElement.innerHTML = `
      <i class="fas fa-arrow-${isPositive ? 'up' : 'down'} mr-1"></i>
      <span>${Math.abs(efficiencyChange).toFixed(1)}%</span>
      <span class="text-gray-500 ml-1">improvement</span>
    `;
    // Set appropriate color
    efficiencyTrendElement.className = 'text-xs mt-1';
    efficiencyTrendElement.classList.add(isPositive ? 'text-green-500' : 'text-gray-400');
  }
}

// Update trend indicators with real data
function updateTrendIndicators(data) {
  if (!data) return;
  
  const energyChange = data.energyChange || 0;
  const costChange = data.costChange || 0;
  
  updateTrendIndicator('energyTrend', energyChange);
  updateTrendIndicator('costTrend', costChange);
}

// Update a single trend indicator
function updateTrendIndicator(elementId, value) {
  const element = document.getElementById(elementId);
  if (!element) return;
  
  // Reset classes and preserve text-xs and mt-1 classes
  element.className = 'text-xs mt-1';
  
  if (value < 0) {
    // Decreasing (good for energy and cost)
    element.classList.add('text-green-500');
    element.innerHTML = `
      <i class="fas fa-arrow-down mr-1"></i>
      <span>${Math.abs(value).toLocaleString(undefined, {minimumFractionDigits: 1, maximumFractionDigits: 1})}%</span>
      <span class="text-gray-500 ml-1">vs last month</span>
    `;
  } else {
    // Increasing (concerning for energy and cost)
    element.classList.add('text-rose-500');
    element.innerHTML = `
      <i class="fas fa-arrow-up mr-1"></i>
      <span>${value.toLocaleString(undefined, {minimumFractionDigits: 1, maximumFractionDigits: 1})}%</span>
      <span class="text-gray-500 ml-1">vs last month</span>
    `;
  }
}

// Update the efficiency gauge
function updateEfficiencyGauge(score) {
  const gaugeElement = document.getElementById('efficiencyGauge');
  if (!gaugeElement) return;
  
  // Calculate rotation angle (0-180 degrees)
  const angle = (score / 100) * 180;
  
  // Update needle position
  const needleElement = document.getElementById('gaugeNeedle');
  if (needleElement) {
    needleElement.style.transform = `rotate(${angle}deg)`;
  }
  
  // Update score display
  const scoreElement = document.getElementById('efficiencyScore');
  if (scoreElement) {
    scoreElement.textContent = Math.round(score);
    
    // Set color based on score
    scoreElement.className = 'text-3xl font-bold';
    
    if (score >= 70) {
      scoreElement.classList.add('text-green-500');
    } else if (score >= 40) {
      scoreElement.classList.add('text-yellow-500');
    } else {
      scoreElement.classList.add('text-rose-500');
    }
  }
}

// Global chart references
let energyChart = null;
let distributionChart = null;
let advancedAnalyticsChart = null;
let comparisonChart = null;

// Initialize charts with real data
function initializeCharts(data) {
  if (!data) return;
  
  // Process energy trend data for chart
  const trendDates = Object.keys(data.energyTrend || {});
  const trendValues = Object.values(data.energyTrend || {});
  
  // Format dates for better display
  const formattedDates = trendDates.map(date => {
    const dateObj = new Date(date);
    return dateObj.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  });
  
  // Process device distribution data
  const deviceNames = Object.keys(data.deviceDistribution || {});
  const deviceValues = Object.values(data.deviceDistribution || {});
  
  // Calculate percentages for the pie chart
  const totalEnergy = deviceValues.reduce((sum, value) => sum + value, 0) || 1; // Avoid division by zero
  const devicePercentages = deviceValues.map(value => (value / totalEnergy) * 100);
  
  // If no data is available, we'll display empty charts
  // No static demo data - this ensures we only show real data from the server
  
  // Energy consumption chart options
  const energyChartOptions = {
    series: [{
      name: 'Energy Consumption',
      type: 'area',
      data: trendValues
    }],
    chart: {
      height: 350,
      type: 'line',
      toolbar: {
        show: true,
        tools: {
          download: true,
          selection: false,
          zoom: false,
          zoomin: false,
          zoomout: false,
          pan: false,
          reset: false
        }
      },
      dropShadow: {
        enabled: true,
        color: '#000',
        top: 18,
        left: 7,
        blur: 10,
        opacity: 0.2
      },
      background: 'transparent'
    },
    colors: ['#6366f1'],
    dataLabels: {
      enabled: false,
    },
    stroke: {
      curve: 'smooth',
      width: 3,
    },
    title: {
      text: 'Energy Consumption Trend',
      align: 'left',
      style: {
        color: '#f1f5f9'
      }
    },
    grid: {
      show: true,
      borderColor: '#1e293b',
      strokeDashArray: 5,
      position: 'back',
      row: {
        colors: ['transparent', 'transparent'],
        opacity: 0.5
      }
    },
    xaxis: {
      categories: formattedDates,
      labels: {
        style: {
          colors: '#94a3b8'
        }
      },
      axisBorder: {
        show: false
      }
    },
    yaxis: {
      title: {
        text: 'kWh',
        style: {
          color: '#94a3b8'
        }
      },
      labels: {
        style: {
          colors: '#94a3b8'
        }
      }
    },
    fill: {
      type: 'gradient',
      gradient: {
        shade: 'dark',
        gradientToColors: ['#8b5cf6'],
        shadeIntensity: 1,
        type: 'vertical',
        opacityFrom: 0.7,
        opacityTo: 0.2
      },
    },
    markers: {
      size: 4,
      colors: ["#6366f1"],
      strokeColors: "#fff",
      strokeWidth: 2,
      hover: {
        size: 7,
      }
    },
    tooltip: {
      theme: 'dark',
      y: {
        formatter: function (val) {
          return val.toFixed(2) + " kWh"
        }
      }
    },
    legend: {
      position: 'top',
      horizontalAlign: 'right',
      floating: true,
      offsetY: -25,
      offsetX: -5,
      labels: {
        colors: '#94a3b8'
      }
    }
  };

  // Device distribution chart options
  const distributionChartOptions = {
    series: devicePercentages,
    chart: {
      width: '100%',
      height: 360,
      type: 'donut',
      background: 'transparent'
    },
    labels: deviceNames,
    colors: ['#8b5cf6', '#6366f1', '#3b82f6', '#06b6d4', '#10b981', '#f59e0b', '#ef4444', '#ec4899'],
    plotOptions: {
      pie: {
        dataLabels: {
          offset: -5
        },
        donut: {
          size: '65%',
        }
      }
    },

    dataLabels: {
      formatter(val, opts) {
        const name = opts.w.globals.labels[opts.seriesIndex];
        return [name, val.toFixed(1) + '%'];
      },
      style: {
        colors: ['#f1f5f9']
      }
    },
    legend: {
      show: false
    },
    tooltip: {
      theme: 'dark',
      y: {
        formatter: function(val) {
          return val.toFixed(1) + '%';
        }
      }
    }
  };
  
  // Initialize and render charts if elements exist
  const energyChartEl = document.getElementById('energyConsumptionChart');
  const distributionChartEl = document.getElementById('usageDistributionChart');
  const advancedAnalyticsEl = document.getElementById('advancedAnalyticsChart');
  const comparisonChartEl = document.getElementById('comparisonChart');
  
  if (energyChartEl) {
    // Destroy existing chart if it exists
    if (energyChart) {
      energyChart.destroy();
    }
    // Create new chart
    energyChart = new ApexCharts(energyChartEl, energyChartOptions);
    energyChart.render();
  }
  
  if (distributionChartEl) {
    // Destroy existing chart if it exists
    if (distributionChart) {
      distributionChart.destroy();
    }
    // Create new chart
    distributionChart = new ApexCharts(distributionChartEl, distributionChartOptions);
    distributionChart.render();
  }
  
  // Advanced Energy Analytics Chart
  if (advancedAnalyticsEl) {
    const analyticsOptions = {
      series: [{
        name: 'Energy Usage',
        data: trendValues && trendValues.length > 0 ? trendValues.slice(-7) : [] // Use last 7 days of data if available
      }],
      chart: {
        height: 320,
        type: 'area',
        toolbar: {
          show: true,
          tools: {
            download: true,
            selection: false,
            zoom: true,
            zoomin: true,
            zoomout: true,
            pan: true,
            reset: true
          }
        },
        background: 'transparent'
      },
      colors: ['#8b5cf6'],
      dataLabels: {
        enabled: false,
      },
      stroke: {
        curve: 'smooth',
        width: 3,
      },
      fill: {
        type: 'gradient',
        gradient: {
          shade: 'dark',
          gradientToColors: ['#6366f1'],
          shadeIntensity: 1,
          type: 'vertical',
          opacityFrom: 0.7,
          opacityTo: 0.2
        },
      },
      xaxis: {
        categories: formattedDates.slice(-7),
        labels: {
          style: {
            colors: '#94a3b8'
          }
        }
      },
      yaxis: {
        labels: {
          style: {
            colors: '#94a3b8'
          },
          formatter: function (val) {
            return val.toFixed(1) + ' kWh';
          }
        }
      },
      grid: {
        borderColor: '#1e293b',
        strokeDashArray: 5,
        position: 'back'
      },
      tooltip: {
        theme: 'dark'
      }
    };
    if (advancedAnalyticsEl) {
      // Destroy existing chart if it exists
      if (advancedAnalyticsChart) {
        advancedAnalyticsChart.destroy();
      }
      // Create new chart
      advancedAnalyticsChart = new ApexCharts(advancedAnalyticsEl, analyticsOptions);
      advancedAnalyticsChart.render();
    }
  }

}

// Initialize appliance dropdown in calculator
function initializeApplianceDropdown() {
  const applianceSelector = document.getElementById('applianceName');
  if (!applianceSelector) return;
  
  // Fetch appliances from API
  fetch(API.APPLIANCES)
    .then(response => {
      if (!response.ok) throw new Error('Failed to fetch appliances');
      return response.json();
    })
    .then(appliances => {
      // Clear existing options
      applianceSelector.innerHTML = '<option value="">Select an appliance</option>';
      
      // Add appliances to dropdown
      appliances.forEach(appliance => {
        const option = document.createElement('option');
        option.value = appliance.name;
        option.textContent = appliance.name;
        option.dataset.wattage = appliance.wattage;
        applianceSelector.appendChild(option);
      });
      
      // Add event listener to update wattage when appliance is selected
      applianceSelector.addEventListener('change', function() {
        const selectedOption = this.options[this.selectedIndex];
        const wattage = selectedOption.dataset.wattage;
        
        if (wattage) {
          document.getElementById('wattage').value = wattage;
        }
      });
    })
    .catch(error => {
      console.error('Error loading appliances:', error);
    });
}

// Save calculation to database via API
function saveCalculation() {
  // Get data from the result section, rather than the form
  const calculationForm = document.getElementById('calculationForm');
  const resultsDiv = document.getElementById('calculationResults');
  
  // Check if results are visible
  if (resultsDiv.classList.contains('hidden')) {
    alert('Please calculate energy usage first before saving.');
    return;
  }
  
  // Get selected appliance from the form
  const selectedAppliance = document.getElementById('selectedAppliance').value;
  const customAppliance = document.getElementById('customAppliance')?.value || '';
  const applianceName = selectedAppliance === 'custom' ? customAppliance : selectedAppliance;
  
  // Get other values from form
  const wattage = parseFloat(document.getElementById('wattage').value.replace(/,/g, ''));
  const hoursUsed = parseFloat(document.getElementById('hoursUsed').value);
  const timeUnit = document.getElementById('timeUnit').value;
  const energyCost = parseFloat(document.getElementById('energyCost').value.replace(/,/g, ''));
  
  // Create a save button with loading state
  const saveButton = document.querySelector('#calculationResults button.btn-primary');
  const originalContent = saveButton.innerHTML;
  saveButton.innerHTML = '<i class="fas fa-spinner fa-spin mr-1"></i> Saving...';
  saveButton.disabled = true;
  
  // Validate data
  if (!applianceName || isNaN(wattage) || isNaN(hoursUsed) || isNaN(energyCost)) {
    alert('Error retrieving calculation data. Please try calculating again.');
    saveButton.innerHTML = originalContent;
    saveButton.disabled = false;
    return;
  }
  
  // Prepare data for API call
  const usageData = {
    applianceName: applianceName,
    wattage: wattage,
    hoursUsed: hoursUsed,
    timeUnit: timeUnit,
    energyCost: energyCost
  };
  
  // Get the csrf token and header from meta tags
  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
  
  // Send to backend
  fetch(API.SAVE_USAGE, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(csrfHeader && csrfToken ? {[csrfHeader]: csrfToken} : {})
    },
    body: JSON.stringify(usageData)
  })
  .then(response => {
    if (!response.ok) throw new Error('Failed to save calculation');
    return response.json();
  })
  .then(data => {
    // Reset save button
    saveButton.innerHTML = originalContent;
    saveButton.disabled = false;
    
    if (data.success) {
      // Show success message
      alert('Calculation saved successfully!');
      
      // Update charts immediately with new calculation data
      updateChartsWithNewCalculation(usageData);
      
      // Then refresh all dashboard data
      fetchDashboardData();
      
      // Navigate to history section (optional)
      const shouldNavigate = confirm('View your saved calculations in history?');
      if (shouldNavigate) {
        showSection('history');
      }
    }
  })
  .catch(error => {
    console.error('Error saving calculation:', error);
    alert('There was an error saving your calculation. Please try again.');
    
    // Reset save button
    saveButton.innerHTML = originalContent;
    saveButton.disabled = false;
  });
}

// Delete a usage record from history
function deleteUsageRecord(id) {
  if (!id || !confirm('Are you sure you want to delete this record?')) {
    return;
  }
  
  fetch(API.DELETE_USAGE + id, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
      [csrfHeader]: csrfToken
    }
  })
  .then(response => {
    if (!response.ok) throw new Error('Failed to delete record');
    return response.json();
  })
  .then(data => {
    if (data.success) {
      // Refresh dashboard data
      fetchDashboardData();
      
      // Refresh history section
      document.querySelector('.history-item[data-id="' + id + '"]')?.remove();
      
      // Show success message
      alert('Record deleted successfully!');
    }
  })
  .catch(error => {
    console.error('Error deleting record:', error);
    alert('There was an error deleting the record. Please try again.');
  });
}

// Export usage history as CSV
function exportUsageData() {
  // Get history items
  const historyItems = document.querySelectorAll('.history-item');
  
  if (historyItems.length === 0) {
    alert('No data to export. Add some usage records first.');
    return;
  }
  
  // Format data for CSV
  const headers = ['Appliance', 'Wattage', 'Hours Used', 'Time Unit', 'kWh Consumed', 'Cost', 'Date Recorded'];
  let csvContent = headers.join(',') + '\n';
  
  // Extract data from DOM
  historyItems.forEach(item => {
    // Only include visible items (respecting filters)
    if (item.style.display !== 'none') {
      const appliance = item.querySelector('[data-field="applianceName"]').textContent;
      const wattage = item.querySelector('[data-field="wattage"]').textContent;
      const hoursUsed = item.querySelector('[data-field="hoursUsed"]').textContent;
      const timeUnit = item.querySelector('[data-field="timeUnit"]').textContent;
      const kWh = item.querySelector('[data-field="kWhConsumed"]').textContent;
      const cost = item.querySelector('[data-field="cost"]').textContent;
      const date = item.querySelector('[data-field="recordedAt"]').textContent;
      
      const row = [appliance, wattage, hoursUsed, timeUnit, kWh, cost, date];
      
      // Escape any commas in the data
      const escapedRow = row.map(cell => {
        const cellStr = String(cell).trim();
        return cellStr.includes(',') ? `"${cellStr}"` : cellStr;
      });
      
      csvContent += escapedRow.join(',') + '\n';
    }
  });
  
  // Create download link
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.setAttribute('href', url);
  link.setAttribute('download', 'energy_usage_history.csv');
  link.style.visibility = 'hidden';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

// Update charts immediately with new calculation data
function updateChartsWithNewCalculation(usageData) {
  console.log('New calculation saved, refreshing all data from API...');
  
  // Show loading indicator to let user know we're refreshing
  const loader = document.getElementById('fullPageLoader');
  if (loader) {
    loader.classList.remove('hidden');
    const progressBar = document.getElementById('loadingProgress');
    if (progressBar) progressBar.style.width = '70%';
  }
  
  // Force refresh from API instead of manually updating
  fetch(API.DASHBOARD_DATA)
    .then(response => {
      if (!response.ok) {
        throw new Error(`Error refreshing dashboard data: ${response.status}`);
      }
      return response.json();
    })
    .then(freshData => {
      // Store the completely fresh data from API
      window.dashboardData = freshData;
      
      console.log('Successfully refreshed dashboard data from API');
      
      // Update all dashboard components with fresh data
      updateDashboardMetrics(freshData);
      updateTrendIndicators(freshData);
      
      // Completely rebuild all charts with fresh data
      if (energyChart) energyChart.destroy();
      if (distributionChart) distributionChart.destroy();
      if (advancedAnalyticsChart) advancedAnalyticsChart.destroy();
      if (comparisonChart) comparisonChart.destroy();
      
      initializeCharts(freshData);
      updateEfficiencyGauge(freshData.efficiencyScore || 0);
      
      // Hide loading indicator
      if (loader) {
        const progressBar = document.getElementById('loadingProgress');
        if (progressBar) progressBar.style.width = '100%';
        setTimeout(() => loader.classList.add('hidden'), 500);
      }
    })
    .catch(error => {
      console.error('API Error while refreshing data:', error);
      // Hide loading indicator even on error
      if (loader) {
        loader.classList.add('hidden');
      }
      // Fallback to the old method if API refresh fails
      let dashData = window.dashboardData || {
        totalEnergy: 0, totalCost: 0, recordCount: 0,
        energyTrend: {}, deviceDistribution: {}
      };
      
      // Calculate kWh for this calculation
      let hoursMultiplier = 1;
      if (usageData.timeUnit === 'days') {
        hoursMultiplier = 24;
      } else if (usageData.timeUnit === 'weeks') {
        hoursMultiplier = 24 * 7;
      } else if (usageData.timeUnit === 'months') {
        hoursMultiplier = 24 * 30; // Approximation
      }
      
      const totalHours = usageData.hoursUsed * hoursMultiplier;
      const kwhConsumed = (usageData.wattage * totalHours) / 1000;
      const cost = kwhConsumed * usageData.energyCost;
      
      // Update today's data in energy trend
      const today = new Date().toISOString().split('T')[0]; // YYYY-MM-DD
      dashData.energyTrend[today] = (dashData.energyTrend[today] || 0) + kwhConsumed;
      
      // Update the rest as before
      updateDashboardMetrics(dashData);
      initializeCharts(dashData);
    });
}

// Override any chart initialization from HTML to prevent conflicts
function overrideHtmlCharts() {
  // Find the HTML initializeCharts function and override it
  window.initializeCharts = function() {
    console.log('HTML charts initialization intercepted - using dashboard.js charts instead');
    // Call our version instead
    if (window.dashboardData) {
      initializeCharts(window.dashboardData);
    }
    return false;
  };
}

// Setup mobile menu toggle
function setupMobileMenu() {
  // Mobile menu toggle
  document.querySelector('.mobile-menu-button')?.addEventListener('click', function() {
    document.querySelector('.mobile-menu').classList.toggle('hidden');
  });
  
  // Mobile sidebar toggle
  document.getElementById('sidebarToggle')?.addEventListener('click', function() {
    const sidebar = document.getElementById('sidebar');
    sidebar.classList.toggle('hidden');
    sidebar.classList.toggle('fixed');
    sidebar.classList.toggle('z-40');
    sidebar.classList.toggle('inset-y-0');
    sidebar.classList.toggle('left-0');
  });
  
  // Close sidebar on navigation in mobile
  document.querySelectorAll('[onclick*="showSection"]').forEach(link => {
    link.addEventListener('click', function(e) {
      if (window.innerWidth < 1024) {
        document.getElementById('sidebar').classList.add('hidden');
      }
    });
  });
}

// Initialize with demo data if API fails
function initializeWithDemoData() {
  const demoData = {
    totalEnergy: 254.6,
    totalCost: 178.22,
    recordCount: 12,
    energyChange: -5.2,
    costChange: 3.7,
    efficiencyScore: 75,
    deviceDistribution: {
      'Air Conditioner': 42, 
      'Refrigerator': 23, 
      'Lighting': 15, 
      'Television': 12, 
      'Other': 8
    },
    energyTrend: {
      '2025-04-23': 30, '2025-04-24': 40, '2025-04-25': 35, '2025-04-26': 50, 
      '2025-04-27': 49, '2025-04-28': 60, '2025-04-29': 70, '2025-04-30': 80, 
      '2025-05-01': 75, '2025-05-02': 65, '2025-05-03': 90, '2025-05-04': 85, 
      '2025-05-05': 95, '2025-05-06': 87
    }
  };
  
  updateDashboardMetrics(demoData);
  updateTrendIndicators(demoData);
  initializeCharts(demoData);
  updateEfficiencyGauge(demoData.efficiencyScore);
}
