import React, { useState } from 'react';
import {
  Search,
  Filter,
  MapPin,
  Calendar,
  ShieldCheck,
  Tag,
  CheckCircle2,
  Clock,
  HelpCircle,
  Eye,
  PlusCircle
} from 'lucide-react';
import { FoundReport, LostReport, CampusLocation, ItemCategory } from '../types';

interface BrowseItemsViewProps {
  foundItems: FoundReport[];
  lostItems: LostReport[];
  locations: CampusLocation[];
  categories: ItemCategory[];
  onClaimItem: (item: FoundReport) => void;
  onViewDetails: (item: FoundReport) => void;
  onNavigate: (view: string) => void;
}

export const BrowseItemsView: React.FC<BrowseItemsViewProps> = ({
  foundItems,
  lostItems,
  locations,
  categories,
  onClaimItem,
  onViewDetails,
  onNavigate
}) => {
  const [activeTab, setActiveTab] = useState<'FOUND' | 'LOST'>('FOUND');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');
  const [selectedLocation, setSelectedLocation] = useState<string>('ALL');

  // Filter Found Items
  const filteredFound = foundItems.filter(item => {
    const matchesCat = selectedCategory === 'ALL' || item.category.toLowerCase() === selectedCategory.toLowerCase();
    const matchesLoc = selectedLocation === 'ALL' || (item.locationId && item.locationId.toString() === selectedLocation);
    const q = searchQuery.toLowerCase();
    const matchesQuery = !searchQuery ||
      item.title.toLowerCase().includes(q) ||
      item.category.toLowerCase().includes(q) ||
      (item.color && item.color.toLowerCase().includes(q)) ||
      (item.brand && item.brand.toLowerCase().includes(q)) ||
      (item.publicDescription && item.publicDescription.toLowerCase().includes(q)) ||
      item.locationName.toLowerCase().includes(q);
    return matchesCat && matchesLoc && matchesQuery;
  });

  // Filter Lost Items
  const filteredLost = lostItems.filter(item => {
    const matchesCat = selectedCategory === 'ALL' || item.category.toLowerCase() === selectedCategory.toLowerCase();
    const matchesLoc = selectedLocation === 'ALL' || (item.locationId && item.locationId.toString() === selectedLocation);
    const q = searchQuery.toLowerCase();
    const matchesQuery = !searchQuery ||
      item.title.toLowerCase().includes(q) ||
      item.category.toLowerCase().includes(q) ||
      (item.color && item.color.toLowerCase().includes(q)) ||
      (item.brand && item.brand.toLowerCase().includes(q)) ||
      item.description.toLowerCase().includes(q) ||
      item.locationName.toLowerCase().includes(q);
    return matchesCat && matchesLoc && matchesQuery;
  });

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
        <div>
          <h1 className="text-3xl font-black text-slate-900 tracking-tight">
            Browse Campus Items
          </h1>
          <p className="text-sm text-slate-600 mt-1">
            Search verified found items or check active lost reports filed across campus buildings.
          </p>
        </div>

        {/* Tab Switcher */}
        <div className="inline-flex p-1 bg-slate-200/80 rounded-2xl self-start md:self-auto">
          <button
            onClick={() => setActiveTab('FOUND')}
            className={`px-5 py-2 rounded-xl text-xs font-bold transition-all ${
              activeTab === 'FOUND'
                ? 'bg-white text-emerald-700 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Found Items ({filteredFound.length})
          </button>
          <button
            onClick={() => setActiveTab('LOST')}
            className={`px-5 py-2 rounded-xl text-xs font-bold transition-all ${
              activeTab === 'LOST'
                ? 'bg-white text-amber-700 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Lost Reports ({filteredLost.length})
          </button>
        </div>
      </div>

      {/* Filter & Search Bar */}
      <div className="bg-white rounded-3xl p-5 border border-slate-200 shadow-sm mb-8 space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-12 gap-3">
          {/* Natural Language Search Input */}
          <div className="md:col-span-6 relative">
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3.5" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search by keyword, e.g. 'black wallet near library' or 'blue bottle'..."
              className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500 font-medium"
            />
          </div>

          {/* Category Filter */}
          <div className="md:col-span-3">
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500 font-medium"
            >
              <option value="ALL">All Categories</option>
              {categories.map((c) => (
                <option key={c.id} value={c.name}>
                  {c.name}
                </option>
              ))}
            </select>
          </div>

          {/* Location Filter */}
          <div className="md:col-span-3">
            <select
              value={selectedLocation}
              onChange={(e) => setSelectedLocation(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500 font-medium"
            >
              <option value="ALL">All Campus Locations</option>
              {locations.map((loc) => (
                <option key={loc.id} value={loc.id.toString()}>
                  {loc.name}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Category Pills */}
        <div className="flex items-center space-x-2 overflow-x-auto pb-1 text-xs">
          <span className="text-slate-400 font-semibold mr-1 flex items-center space-x-1">
            <Tag className="w-3.5 h-3.5" />
            <span>Filter:</span>
          </span>
          <button
            onClick={() => setSelectedCategory('ALL')}
            className={`px-3 py-1 rounded-lg font-bold transition-colors ${
              selectedCategory === 'ALL'
                ? 'bg-brand-600 text-white'
                : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
            }`}
          >
            All
          </button>
          {categories.slice(0, 8).map((cat) => (
            <button
              key={cat.id}
              onClick={() => setSelectedCategory(cat.name)}
              className={`px-3 py-1 rounded-lg font-bold transition-colors whitespace-nowrap ${
                selectedCategory.toLowerCase() === cat.name.toLowerCase()
                  ? 'bg-brand-600 text-white'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              }`}
            >
              {cat.name}
            </button>
          ))}
        </div>
      </div>

      {/* Item Grid */}
      {activeTab === 'FOUND' ? (
        filteredFound.length === 0 ? (
          <div className="bg-white rounded-3xl p-12 border border-slate-200 text-center">
            <div className="w-16 h-16 bg-slate-100 text-slate-400 rounded-2xl flex items-center justify-center mx-auto mb-4">
              <Search className="w-8 h-8" />
            </div>
            <h3 className="text-base font-bold text-slate-800">No matching found items</h3>
            <p className="text-xs text-slate-500 mt-1 max-w-sm mx-auto">
              Try adjusting your search terms or filters. If you lost an item that hasn't been turned in yet, file a lost report.
            </p>
            <button
              onClick={() => onNavigate('report-lost')}
              className="mt-5 px-5 py-2.5 rounded-xl bg-amber-600 hover:bg-amber-700 text-white text-xs font-bold shadow transition-colors"
            >
              Report Lost Item
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredFound.map((item) => (
              <div
                key={item.id}
                className="bg-white rounded-3xl border border-slate-200 overflow-hidden shadow-sm hover:shadow-md transition-all flex flex-col group"
              >
                {/* Image & Badges */}
                <div className="relative h-48 bg-slate-100 overflow-hidden">
                  <img
                    src={item.primaryImageUrl || 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80'}
                    alt={item.title}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  />
                  <div className="absolute top-3 left-3 flex flex-wrap gap-1.5">
                    <span className="px-2.5 py-1 rounded-full text-[11px] font-bold bg-white/95 backdrop-blur text-slate-800 shadow-sm">
                      {item.category}
                    </span>
                    {item.color && (
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-slate-900/80 backdrop-blur text-white shadow-sm">
                        {item.color}
                      </span>
                    )}
                  </div>
                  <div className="absolute top-3 right-3">
                    <span className={`px-2.5 py-1 rounded-full text-[10px] font-bold shadow-sm ${
                      item.status === 'RETURNED'
                        ? 'bg-emerald-600 text-white'
                        : item.status === 'READY_FOR_PICKUP'
                        ? 'bg-purple-600 text-white'
                        : 'bg-blue-600 text-white'
                    }`}>
                      {item.status.replace(/_/g, ' ')}
                    </span>
                  </div>
                </div>

                {/* Content */}
                <div className="p-5 flex-1 flex flex-col justify-between">
                  <div>
                    <div className="flex items-center justify-between text-[11px] text-slate-500 font-mono font-semibold mb-1.5">
                      <span>Ref: {item.referenceId}</span>
                      <span>{new Date(item.foundDate).toLocaleDateString()}</span>
                    </div>

                    <h3 className="font-bold text-slate-900 text-base group-hover:text-brand-600 transition-colors line-clamp-1">
                      {item.title}
                    </h3>

                    <p className="text-xs text-slate-600 mt-1 line-clamp-2 leading-relaxed">
                      {item.publicDescription || 'No public description.'}
                    </p>

                    {item.distinctiveFeatures && (
                      <p className="text-[11px] text-slate-500 mt-2 bg-slate-50 p-2 rounded-xl border border-slate-100 line-clamp-1">
                        <span className="font-bold text-slate-700">Features: </span>
                        {item.distinctiveFeatures}
                      </p>
                    )}

                    {/* Location Pin */}
                    <div className="mt-3 flex items-center space-x-1.5 text-xs text-slate-600">
                      <MapPin className="w-3.5 h-3.5 text-red-500 flex-shrink-0" />
                      <span className="font-medium truncate">{item.locationName}</span>
                      {item.specificArea && (
                        <span className="text-slate-400 truncate">• {item.specificArea}</span>
                      )}
                    </div>
                  </div>

                  {/* Privacy Badge & Actions */}
                  <div className="mt-5 pt-4 border-t border-slate-100">
                    <div className="flex items-center space-x-1.5 text-[11px] text-amber-800 bg-amber-50 px-2.5 py-1 rounded-lg mb-3">
                      <ShieldCheck className="w-3.5 h-3.5 text-amber-600 flex-shrink-0" />
                      <span className="truncate">Identifying secrets masked to protect owner</span>
                    </div>

                    <div className="grid grid-cols-2 gap-2">
                      <button
                        onClick={() => onViewDetails(item)}
                        className="w-full py-2 px-3 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-700 font-bold text-xs transition-colors flex items-center justify-center space-x-1"
                      >
                        <Eye className="w-3.5 h-3.5" />
                        <span>View Details</span>
                      </button>

                      {item.status !== 'RETURNED' && item.status !== 'COLLECTED' ? (
                        <button
                          onClick={() => onClaimItem(item)}
                          className="w-full py-2 px-3 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-600 hover:to-teal-700 text-white font-bold text-xs shadow-sm transition-all flex items-center justify-center space-x-1"
                        >
                          <CheckCircle2 className="w-3.5 h-3.5" />
                          <span>Claim This</span>
                        </button>
                      ) : (
                        <span className="w-full py-2 px-3 rounded-xl bg-slate-100 text-slate-500 font-bold text-xs text-center">
                          Returned
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )
      ) : (
        /* Lost Reports Tab */
        filteredLost.length === 0 ? (
          <div className="bg-white rounded-3xl p-12 border border-slate-200 text-center">
            <div className="w-16 h-16 bg-slate-100 text-slate-400 rounded-2xl flex items-center justify-center mx-auto mb-4">
              <HelpCircle className="w-8 h-8" />
            </div>
            <h3 className="text-base font-bold text-slate-800">No active lost reports found</h3>
            <p className="text-xs text-slate-500 mt-1">If you lost an item, submit a report to get notified.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredLost.map((item) => (
              <div
                key={item.id}
                className="bg-white rounded-3xl border border-slate-200 overflow-hidden shadow-sm hover:shadow-md transition-all flex flex-col"
              >
                <div className="p-5 flex-1 flex flex-col justify-between">
                  <div>
                    <div className="flex items-center justify-between text-[11px] text-slate-500 font-mono font-semibold mb-1.5">
                      <span>Ref: {item.referenceId}</span>
                      <span>Lost: {new Date(item.lostDate).toLocaleDateString()}</span>
                    </div>

                    <div className="flex items-center space-x-1.5 mb-2">
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-100 text-amber-800">
                        {item.category}
                      </span>
                      {item.color && (
                        <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-slate-100 text-slate-700">
                          {item.color}
                        </span>
                      )}
                    </div>

                    <h3 className="font-bold text-slate-900 text-base">
                      {item.title}
                    </h3>

                    <p className="text-xs text-slate-600 mt-2 line-clamp-3 leading-relaxed">
                      {item.description}
                    </p>

                    <div className="mt-3 flex items-center space-x-1.5 text-xs text-slate-600">
                      <MapPin className="w-3.5 h-3.5 text-amber-500 flex-shrink-0" />
                      <span className="font-medium truncate">{item.locationName}</span>
                    </div>
                  </div>

                  <div className="mt-5 pt-4 border-t border-slate-100 flex items-center justify-between">
                    <span className="text-[11px] text-slate-500 font-medium">Owner: {item.ownerName}</span>
                    <button
                      onClick={() => onNavigate('matches')}
                      className="px-3 py-1.5 rounded-xl bg-brand-50 hover:bg-brand-100 text-brand-700 text-xs font-bold transition-colors"
                    >
                      Check Matches
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )
      )}
    </div>
  );
};
