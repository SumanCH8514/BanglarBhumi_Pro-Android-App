import React, { useState, useEffect } from 'react';
import { Search, MapPin, Layers, Hash, AlertCircle, Loader2, Sparkles, Filter } from 'lucide-react';
import { DistrictItem, BlockItem, MouzaItem, LandRecordResult } from '../types';

interface SearchFormProps {
  onSearchResult: (result: LandRecordResult) => void;
  lang: 'en' | 'bn';
  presetSelection?: {
    districtCode: string;
    districtName: string;
    blockCode: string;
    blockName: string;
    mouzaCode: string;
    mouzaName: string;
  } | null;
}

export const SearchForm: React.FC<SearchFormProps> = ({
  onSearchResult,
  lang,
  presetSelection
}) => {
  const [districts, setDistricts] = useState<DistrictItem[]>([]);
  const [blocks, setBlocks] = useState<BlockItem[]>([]);
  const [mouzas, setMouzas] = useState<MouzaItem[]>([]);

  const [selectedDistrict, setSelectedDistrict] = useState<string>('');
  const [selectedBlock, setSelectedBlock] = useState<string>('');
  const [selectedMouza, setSelectedMouza] = useState<string>('');

  const [mouzaSearchText, setMouzaSearchText] = useState<string>('');
  const [searchType, setSearchType] = useState<'plot' | 'khatian'>('plot');
  const [searchNumber, setSearchNumber] = useState<string>('');

  const [isLoadingDistricts, setIsLoadingDistricts] = useState<boolean>(true);
  const [isLoadingBlocks, setIsLoadingBlocks] = useState<boolean>(false);
  const [isLoadingMouzas, setIsLoadingMouzas] = useState<boolean>(false);
  const [isSearching, setIsSearching] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string>('');

  useEffect(() => {
    async function loadDistricts() {
      try {
        setIsLoadingDistricts(true);
        const res = await fetch('/api/locations/districts');
        const json = await res.json();
        if (json.success && Array.isArray(json.data)) {
          setDistricts(json.data);
        }
      } catch (err) {
        setErrorMessage('Failed to load districts from local database');
      } finally {
        setIsLoadingDistricts(false);
      }
    }
    loadDistricts();
  }, []);

  useEffect(() => {
    if (!selectedDistrict) {
      setBlocks([]);
      setSelectedBlock('');
      setMouzas([]);
      setSelectedMouza('');
      return;
    }

    async function loadBlocks() {
      try {
        setIsLoadingBlocks(true);
        setBlocks([]);
        setSelectedBlock('');
        setMouzas([]);
        setSelectedMouza('');
        const res = await fetch(`/api/locations/blocks?district=${selectedDistrict}`);
        const json = await res.json();
        if (json.success && Array.isArray(json.data)) {
          setBlocks(json.data);
        }
      } catch (err) {
        setErrorMessage('Failed to load blocks for selected district');
      } finally {
        setIsLoadingBlocks(false);
      }
    }
    loadBlocks();
  }, [selectedDistrict]);

  useEffect(() => {
    if (!selectedDistrict || !selectedBlock) {
      setMouzas([]);
      setSelectedMouza('');
      return;
    }

    async function loadMouzas() {
      try {
        setIsLoadingMouzas(true);
        setMouzas([]);
        setSelectedMouza('');
        const res = await fetch(`/api/locations/mouzas?district=${selectedDistrict}&block=${selectedBlock}`);
        const json = await res.json();
        if (json.success && Array.isArray(json.data)) {
          setMouzas(json.data);
        }
      } catch (err) {
        setErrorMessage('Failed to load mouzas for selected block');
      } finally {
        setIsLoadingMouzas(false);
      }
    }
    loadMouzas();
  }, [selectedDistrict, selectedBlock]);

  useEffect(() => {
    if (presetSelection) {
      setSelectedDistrict(presetSelection.districtCode);
      setTimeout(() => {
        setSelectedBlock(presetSelection.blockCode);
        setTimeout(() => {
          setSelectedMouza(presetSelection.mouzaCode);
        }, 300);
      }, 300);
    }
  }, [presetSelection]);

  const filteredMouzas = mouzas.filter(m => {
    if (!mouzaSearchText.trim()) return true;
    const term = mouzaSearchText.toLowerCase();
    return (
      m.en.toLowerCase().includes(term) ||
      m.bn.includes(term) ||
      m.code.includes(term)
    );
  });

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage('');

    if (!selectedDistrict) {
      setErrorMessage(lang === 'bn' ? 'অনুগ্রহ করে জেলা নির্বাচন করুন।' : 'Please select a district.');
      return;
    }
    if (!selectedBlock) {
      setErrorMessage(lang === 'bn' ? 'অনুগ্রহ করে ব্লক নির্বাচন করুন।' : 'Please select a block.');
      return;
    }
    if (!selectedMouza) {
      setErrorMessage(lang === 'bn' ? 'অনুগ্রহ করে মৌজা নির্বাচন করুন।' : 'Please select a mouza.');
      return;
    }
    if (!searchNumber.trim()) {
      setErrorMessage(
        lang === 'bn'
          ? `${searchType === 'plot' ? 'দাগ' : 'খতিয়ান'} নম্বর প্রবেশ করুন।`
          : `Please enter the ${searchType === 'plot' ? 'Dag / Plot' : 'Khatian'} number.`
      );
      return;
    }

    const currentDist = districts.find(d => d.code === selectedDistrict);
    const currentBlk = blocks.find(b => b.code === selectedBlock);
    const currentMz = mouzas.find(m => m.code === selectedMouza);

    try {
      setIsSearching(true);
      const res = await fetch('/api/land-record/search', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          searchType,
          districtCode: selectedDistrict,
          districtName: currentDist ? `${currentDist.en} (${currentDist.bn})` : selectedDistrict,
          blockCode: selectedBlock,
          blockName: currentBlk ? `${currentBlk.en} (${currentBlk.bn})` : selectedBlock,
          mouzaCode: selectedMouza,
          mouzaName: currentMz ? `${currentMz.en} [${currentMz.code}]` : selectedMouza,
          searchNumber: searchNumber.trim()
        })
      });

      const json = await res.json();
      if (json.success && json.data) {
        onSearchResult(json.data);
      } else {
        setErrorMessage(json.message || 'No record found or server response error.');
      }
    } catch (err) {
      setErrorMessage('Network connection failure. Please check server or retry.');
    } finally {
      setIsSearching(false);
    }
  };

  return (
    <div className="bg-white rounded-2xl shadow-xl shadow-slate-200/50 border border-slate-200 p-6 md:p-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-6 mb-6 border-b border-slate-100 gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900 flex items-center gap-2">
            <Layers className="w-5 h-5 text-emerald-600" />
            <span>{lang === 'bn' ? 'খতিয়ান ও দাগের তথ্য সন্ধান' : 'Know Your Property (Khatian & Plot Record)'}</span>
          </h2>
          <p className="text-sm text-slate-500 mt-1">
            {lang === 'bn'
              ? 'জেলা, ব্লক ও মৌজা নির্বাচন করে খতিয়ান অথবা দাগ নম্বর দিয়ে অনুসন্ধান করুন'
              : 'Select your administrative jurisdiction and search by Khatian or Plot number'}
          </p>
        </div>

        <div className="inline-flex p-1 bg-slate-100 rounded-xl border border-slate-200/80 self-start sm:self-auto">
          <button
            type="button"
            onClick={() => setSearchType('plot')}
            className={`px-4 py-1.5 rounded-lg text-xs font-bold transition-all ${
              searchType === 'plot'
                ? 'bg-white text-emerald-800 shadow-sm shadow-slate-200'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            {lang === 'bn' ? 'দাগের তথ্য (Plot Search)' : 'Search by Plot / Dag'}
          </button>
          <button
            type="button"
            onClick={() => setSearchType('khatian')}
            className={`px-4 py-1.5 rounded-lg text-xs font-bold transition-all ${
              searchType === 'khatian'
                ? 'bg-white text-emerald-800 shadow-sm shadow-slate-200'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            {lang === 'bn' ? 'খতিয়ানের তথ্য (Khatian Search)' : 'Search by Khatian'}
          </button>
        </div>
      </div>

      {errorMessage && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 text-red-800 rounded-xl flex items-start gap-3 text-sm">
          <AlertCircle className="w-5 h-5 text-red-600 shrink-0 mt-0.5" />
          <div>
            <span className="font-semibold">{lang === 'bn' ? 'ত্রুটি:' : 'Notice:'} </span>
            {errorMessage}
          </div>
        </div>
      )}

      <form onSubmit={handleSearch} className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-2 flex items-center gap-1.5">
              <MapPin className="w-3.5 h-3.5 text-emerald-600" />
              <span>{lang === 'bn' ? '১. জেলা নির্বাচন করুন' : '1. Select District'}</span>
            </label>
            <div className="relative">
              <select
                value={selectedDistrict}
                onChange={e => setSelectedDistrict(e.target.value)}
                disabled={isLoadingDistricts}
                className="w-full bg-slate-50 border border-slate-300 hover:border-slate-400 focus:border-emerald-600 focus:ring-2 focus:ring-emerald-600/10 rounded-xl px-4 py-3 text-sm font-medium text-slate-900 transition-colors disabled:opacity-50"
              >
                <option value="">{lang === 'bn' ? '-- জেলা বাছাই করুন --' : '-- Choose District --'}</option>
                {districts.map(d => (
                  <option key={d.code} value={d.code}>
                    {d.en} - {d.bn} ({d.code})
                  </option>
                ))}
              </select>
              {isLoadingDistricts && (
                <div className="absolute right-3 top-3.5">
                  <Loader2 className="w-4 h-4 animate-spin text-slate-400" />
                </div>
              )}
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-2 flex items-center gap-1.5">
              <Layers className="w-3.5 h-3.5 text-emerald-600" />
              <span>{lang === 'bn' ? '২. ব্লক নির্বাচন করুন' : '2. Select Block'}</span>
            </label>
            <div className="relative">
              <select
                value={selectedBlock}
                onChange={e => setSelectedBlock(e.target.value)}
                disabled={!selectedDistrict || isLoadingBlocks}
                className="w-full bg-slate-50 border border-slate-300 hover:border-slate-400 focus:border-emerald-600 focus:ring-2 focus:ring-emerald-600/10 rounded-xl px-4 py-3 text-sm font-medium text-slate-900 transition-colors disabled:opacity-50"
              >
                <option value="">{lang === 'bn' ? '-- ব্লক বাছাই করুন --' : '-- Choose Block --'}</option>
                {blocks.map(b => (
                  <option key={b.code} value={b.code}>
                    {b.en} {b.bn ? `- ${b.bn}` : ''}
                  </option>
                ))}
              </select>
              {isLoadingBlocks && (
                <div className="absolute right-3 top-3.5">
                  <Loader2 className="w-4 h-4 animate-spin text-slate-400" />
                </div>
              )}
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-2 flex items-center gap-1.5">
              <Filter className="w-3.5 h-3.5 text-emerald-600" />
              <span>{lang === 'bn' ? '৩. মৌজা নির্বাচন করুন' : '3. Select Mouza'}</span>
            </label>
            <div className="space-y-2">
              <div className="relative">
                <select
                  value={selectedMouza}
                  onChange={e => setSelectedMouza(e.target.value)}
                  disabled={!selectedBlock || isLoadingMouzas}
                  className="w-full bg-slate-50 border border-slate-300 hover:border-slate-400 focus:border-emerald-600 focus:ring-2 focus:ring-emerald-600/10 rounded-xl px-4 py-3 text-sm font-medium text-slate-900 transition-colors disabled:opacity-50"
                >
                  <option value="">
                    {mouzas.length > 0
                      ? lang === 'bn'
                        ? `-- মোট ${mouzas.length} টি মৌজা হতে বাছাই করুন --`
                        : `-- Choose from ${mouzas.length} Mouzas --`
                      : lang === 'bn'
                      ? '-- প্রথমে ব্লক নির্বাচন করুন --'
                      : '-- Select block first --'}
                  </option>
                  {filteredMouzas.map(m => (
                    <option key={m.code} value={m.code}>
                      JL {m.code} - {m.en} {m.bn ? `(${m.bn})` : ''}
                    </option>
                  ))}
                </select>
                {isLoadingMouzas && (
                  <div className="absolute right-3 top-3.5">
                    <Loader2 className="w-4 h-4 animate-spin text-slate-400" />
                  </div>
                )}
              </div>

              {mouzas.length > 5 && (
                <input
                  type="text"
                  placeholder={lang === 'bn' ? 'মৌজার নাম বা JL নম্বর দিয়ে ফিল্টার করুন...' : 'Filter mouza name or JL no...'}
                  value={mouzaSearchText}
                  onChange={e => setMouzaSearchText(e.target.value)}
                  className="w-full bg-white border border-slate-200 rounded-lg px-3 py-1.5 text-xs text-slate-700 placeholder:text-slate-400 focus:border-emerald-500 focus:outline-none"
                />
              )}
            </div>
          </div>
        </div>

        <div className="pt-2">
          <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-2 flex items-center justify-between">
            <span className="flex items-center gap-1.5">
              <Hash className="w-3.5 h-3.5 text-emerald-600" />
              <span>
                {searchType === 'plot'
                  ? lang === 'bn'
                    ? '৪. দাগ নম্বর প্রবেশ করুন (Plot / Dag Number)'
                    : '4. Enter Plot / Dag Number'
                  : lang === 'bn'
                  ? '৪. খতিয়ান নম্বর প্রবেশ করুন (Khatian Number)'
                  : '4. Enter Khatian Number'}
              </span>
            </span>
            <span className="text-[11px] text-slate-400 font-normal lowercase">
              {searchType === 'plot' ? 'বাটা দাগের জন্য স্লাশ ব্যবহার করুন (উদাঃ 105/1)' : 'খতিয়ান বাটনের ক্ষেত্রে স্লাশ ব্যবহার করতে পারেন'}
            </span>
          </label>
          <div className="flex flex-col sm:flex-row gap-4 items-center">
            <div className="relative flex-1 w-full">
              <input
                type="text"
                value={searchNumber}
                onChange={e => setSearchNumber(e.target.value)}
                placeholder={searchType === 'plot' ? 'Ex: 142 or 142/1' : 'Ex: 489 or 489/2'}
                className="w-full bg-slate-50 border border-slate-300 hover:border-slate-400 focus:border-emerald-600 focus:ring-2 focus:ring-emerald-600/10 rounded-xl px-4 py-3.5 text-base font-bold text-slate-900 placeholder:text-slate-400 placeholder:font-normal transition-colors"
              />
            </div>

            <button
              type="submit"
              disabled={isSearching}
              className="w-full sm:w-auto px-8 py-3.5 bg-gradient-to-r from-emerald-700 to-emerald-600 hover:from-emerald-800 hover:to-emerald-700 text-white font-bold rounded-xl shadow-lg shadow-emerald-700/20 active:scale-[0.99] transition-all flex items-center justify-center gap-2.5 disabled:opacity-60 whitespace-nowrap"
            >
              {isSearching ? (
                <>
                  <Loader2 className="w-5 h-5 animate-spin" />
                  <span>{lang === 'bn' ? 'অনুসন্ধান চলছে...' : 'Fetching Record...'}</span>
                </>
              ) : (
                <>
                  <Search className="w-5 h-5" />
                  <span>{lang === 'bn' ? 'তথ্য অনুসন্ধান করুন' : 'Search Land Record'}</span>
                </>
              )}
            </button>
          </div>
        </div>
      </form>

      <div className="mt-8 pt-6 border-t border-slate-100 flex flex-wrap items-center justify-between gap-4 text-xs text-slate-500">
        <div className="flex items-center gap-2">
          <Sparkles className="w-4 h-4 text-emerald-600" />
          <span>{lang === 'bn' ? 'সরাসরি সরকারি সার্ভারের সাথে যুক্ত' : 'Real-time protocol with Banglarbhumi land records service'}</span>
        </div>
        <div className="flex items-center gap-4">
          <span>{lang === 'bn' ? 'সর্বমোট জেলা: ২৩' : '23 Districts indexed'}</span>
          <span>&bull;</span>
          <span>{lang === 'bn' ? '৩৪১+ টি ব্লক' : '341+ Blocks'}</span>
          <span>&bull;</span>
          <span>{lang === 'bn' ? '৪০,০০০+ মৌজা' : '40,000+ Mouzas'}</span>
        </div>
      </div>
    </div>
  );
};
